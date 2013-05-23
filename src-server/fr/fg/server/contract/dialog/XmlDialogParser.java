/*
Copyright 2010 Jeremie Gottero

This file is part of Fallen Galaxy.

Fallen Galaxy is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Fallen Galaxy is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Fallen Galaxy. If not, see <http://www.gnu.org/licenses/>.
*/

package fr.fg.server.contract.dialog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import fr.fg.server.contract.ContractModel;
import fr.fg.server.data.Ally;
import fr.fg.server.data.Contract;
import fr.fg.server.data.Fleet;
import fr.fg.server.data.Player;
import fr.fg.server.util.LoggingSystem;

public class XmlDialogParser {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	@SuppressWarnings("unchecked")
	public static DialogsModel parse(URL url, ContractModel model) throws Exception {
		SAXBuilder builder = new SAXBuilder(true);
		Document doc = builder.build(url);
		
		Element rootNode = doc.getRootElement();
		DialogsModel dialogsModel = new DialogsModel();
		
		List<Element> keyElements = rootNode.getChildren("key");
		for (int i = 0; i < keyElements.size(); i++) {
			Element keyElement = keyElements.get(i);
			dialogsModel.addKey(parseKey(keyElement, model));
		}
		
		List<Element> dialogElements = rootNode.getChildren("dialog");
		for (int i = 0; i < dialogElements.size(); i++) {
			Element dialogElement = dialogElements.get(i);
			dialogsModel.addDialog(parseDialog(dialogElement, model));
		}
		
		return dialogsModel;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static DialogKey parseKey(Element keyElement,
			ContractModel model) throws Exception {
		String name = keyElement.getAttributeValue("name");
		String methodName = keyElement.getAttributeValue("value");
		
		Method method = getMethodByName(model, methodName);
		
		if (method == null)
			throw new Exception("Illegal callback: '" + methodName + "'.");
		
		return new ReflectionDialogKey(name, model, method, getParameterTypes(method));
	}
	
	@SuppressWarnings("unchecked")
	private static Dialog parseDialog(Element dialogElement,
			ContractModel model) throws Exception {
		String type = dialogElement.getAttributeValue("type");
		Dialog dialog = new Dialog(type);
		
		List<Element> entryElements = dialogElement.getChildren("entry");
		
		for (int i = 0; i < entryElements.size(); i++) {
			Element entryElement = entryElements.get(i);
			dialog.addEntry(parseEntry(entryElement, model));
		}
		
		return dialog;
	}
	
	@SuppressWarnings("unchecked")
	private static DialogEntry parseEntry(Element entryElement,
			ContractModel model) throws Exception {
		String name = entryElement.getAttributeValue("name");
		
		List<Element> optionElements = entryElement.getChildren("option");
		DialogOption[] options = new DialogOption[optionElements.size()];
		
		for (int i = 0; i < options.length; i++) {
			Element optionElement = optionElements.get(i);
			options[i] = parseOption(optionElement, model);
		}
		
		return new DialogEntry(name, options);
	}
	
	private static DialogOption parseOption(Element optionElement,
			ContractModel model) throws Exception {
		String target = optionElement.getAttributeValue("target");
		DialogOptionCondition[] conditions;
		
		if (optionElement.getAttribute("condition") != null) {
			String methodName = optionElement.getAttributeValue("condition");
			
			Method method = getMethodByName(model, methodName);
			
			if (method == null)
				throw new Exception("Illegal callback: '" + methodName + "'.");
			
			if (!method.getReturnType().equals(boolean.class) &&
					!method.getReturnType().equals(Boolean.class))
				throw new Exception("Illegal callback return type: '" + method.getReturnType() + "'.");
			
			conditions = new DialogOptionCondition[]{
				new ReflectionDialogOptionCondition(model, method, getParameterTypes(method))
			};
		} else {
			conditions = new DialogOptionCondition[0];
		}
		
		return new DialogOption(target, conditions);
	}
	
	private static Method getMethodByName(ContractModel model, String methodName) {
		Method[] methods = model.getClass().getMethods();
		
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		
		return null;
	}
	
	private static ParameterType[] getParameterTypes(Method method) throws Exception {
		Class<?>[] types = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		ParameterType[] parameterTypes = new ParameterType[types.length];
		
		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			if (type.equals(Contract.class)) {
				parameterTypes[i] = ParameterType.CONTRACT;
			} else if (type.equals(Player.class)) {
				parameterTypes[i] = ParameterType.ATTENDEE_PLAYER;
			} else if (type.equals(Ally.class)) {
				parameterTypes[i] = ParameterType.ATTENDEE_ALLY;
			} else if (type.equals(Fleet.class)) {
				for (Annotation annotation : annotations[i]) {
					if (annotation.annotationType().equals(Parameter.class)) {
						parameterTypes[i] = ((Parameter) annotation).value();
						break;
					}
				}
			} else {
				throw new Exception("Illegal callback parameter: '" + types[i] + "'.");
			}
		}
		
		return parameterTypes;
	}
	
	private static Object invoke(ContractModel model, Method method,
			ParameterType[] parameterTypes, Contract contract,
			Fleet source, Fleet target) {
		try {
			Object[] args = new Object[parameterTypes.length];
			for (int i = 0; i < args.length; i++) {
				if (parameterTypes[i] == ParameterType.CONTRACT)
					args[i] = contract;
				else if (parameterTypes[i] == ParameterType.ATTENDEE_PLAYER)
					args[i] = source.getOwner();
				else if (parameterTypes[i] == ParameterType.ATTENDEE_ALLY)
					args[i] = source.getOwner().getAlly();
				else if (parameterTypes[i] == ParameterType.SOURCE_FLEET)
					args[i] = source;
				else if (parameterTypes[i] == ParameterType.TARGET_FLEET)
					args[i] = target;
			}
			
			return method.invoke(model, args);
		} catch (Exception e) {
			LoggingSystem.getServerLogger().warn(
				"Could not check dialog option validity.", e);
			return false;
		}
	}
	
	private static class ReflectionDialogOptionCondition
			implements DialogOptionCondition {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private ContractModel model;
		
		private Method method;
		
		private ParameterType[] parameterTypes;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ReflectionDialogOptionCondition(ContractModel model,
				Method method, ParameterType[] parameterTypes) {
			this.model = model;
			this.method = method;
			this.parameterTypes = parameterTypes;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		public boolean isValid(Contract contract, Fleet source, Fleet target) {
			return (Boolean) invoke(model, method, parameterTypes,
					contract, source, target);
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
	
	private static class ReflectionDialogKey extends DialogKey {
		// --------------------------------------------------- CONSTANTES -- //
		// ---------------------------------------------------- ATTRIBUTS -- //
		
		private ContractModel model;
		
		private Method method;
		
		private ParameterType[] parameterTypes;
		
		// ------------------------------------------------ CONSTRUCTEURS -- //
		
		public ReflectionDialogKey(String name, ContractModel model,
				Method method, ParameterType[] parameterTypes) {
			super(name);
			this.model = model;
			this.method = method;
			this.parameterTypes = parameterTypes;
		}
		
		// ----------------------------------------------------- METHODES -- //
		
		@Override
		public String getValue(Contract contract, Fleet source, Fleet target) {
			return String.valueOf(invoke(model, method, parameterTypes,
					contract, source, target));
		}
		
		// --------------------------------------------- METHODES PRIVEES -- //
	}
}
