/*
Copyright 2010 Jeremie Gottero, Nicolas Bosc

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

package fr.fg.server.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.fg.server.data.ContractParameter;
import fr.fg.server.data.ContractParameterValue;
import fr.fg.server.data.DataAccess;

public class DataHelper {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	// Récupère la valeur d'un paramètre de type "value"
	public static String getPlayerStringParameter(long idContract, int idPlayer,
			String name) {
		return getStringParameter(idContract, idPlayer, 0, name);
	}
	
	public static String getPlayerNotNullStringParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullStringParameter(idContract, idPlayer, 0, name);
	}
	
	public static Boolean getPlayerBooleanParameter(long idContract,
			int idPlayer, String name) {
		return getBooleanParameter(idContract, idPlayer, 0, name);
	}
	
	public static boolean getPlayerNotNullBooleanParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullBooleanParameter(idContract, idPlayer, 0, name);
	}
	
	public static Integer getPlayerIntParameter(long idContract,
			int idPlayer, String name) {
		return getIntParameter(idContract, idPlayer, 0, name);
	}
	
	public static int getPlayerNotNullIntParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullIntParameter(idContract, idPlayer, 0, name);
	}
	
	public static Long getPlayerLongParameter(long idContract,
			int idPlayer, String name) {
		return getLongParameter(idContract, idPlayer, 0, name);
	}
	
	public static long getPlayerNotNullLongParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullLongParameter(idContract, idPlayer, 0, name);
	}

	public static Double getPlayerDoubleParameter(long idContract,
			int idPlayer, String name) {
		return getDoubleParameter(idContract, idPlayer, 0, name);
	}
	
	public static double getPlayerNotNullDoubleParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullDoubleParameter(idContract, idPlayer, 0, name);
	}
	
	public static String getAllyStringParameter(long idContract, int idAlly,
			String name) {
		return getStringParameter(idContract, 0, idAlly, name);
	}
	
	public static String getAllyNotNullStringParameter(long idContract,
			int idAlly, String name) {
		return getNotNullStringParameter(idContract, 0, idAlly, name);
	}
	
	public static Boolean getAllyBooleanParameter(long idContract,
			int idAlly, String name) {
		return getBooleanParameter(idContract, 0, idAlly, name);
	}
	
	public static boolean getAllyNotNullBooleanParameter(long idContract,
			int idAlly, String name) {
		return getNotNullBooleanParameter(idContract, 0, idAlly, name);
	}
	
	public static Integer getAllyIntParameter(long idContract,
			int idAlly, String name) {
		return getIntParameter(idContract, 0, idAlly, name);
	}
	
	public static int getAllyNotNullIntParameter(long idContract,
			int idAlly, String name) {
		return getNotNullIntParameter(idContract, 0, idAlly, name);
	}
	
	public static Long getAllyLongParameter(long idContract,
			int idAlly, String name) {
		return getLongParameter(idContract, 0, idAlly, name);
	}
	
	public static long getAllyNotNullLongParameter(long idContract,
			int idAlly, String name) {
		return getNotNullLongParameter(idContract, 0, idAlly, name);
	}

	public static Double getAllyDoubleParameter(long idContract,
			int idAlly, String name) {
		return getDoubleParameter(idContract, 0, idAlly, name);
	}
	
	public static double getAllyNotNullDoubleParameter(long idContract,
			int idAlly, String name) {
		return getNotNullDoubleParameter(idContract, 0, idAlly, name);
	}
	
	public static String getContractStringParameter(long idContract,
			String name) {
		return getStringParameter(idContract, 0, 0, name);
	}
	
	public static String getContractNotNullStringParameter(long idContract,
			String name) {
		return getNotNullStringParameter(idContract, 0, 0, name);
	}
	
	public static Boolean getContractBooleanParameter(long idContract,
			String name) {
		return getBooleanParameter(idContract, 0, 0, name);
	}
	
	public static boolean getContractNotNullBooleanParameter(long idContract,
			String name) {
		return getNotNullBooleanParameter(idContract, 0, 0, name);
	}
	
	public static Integer getContractIntParameter(long idContract,
			String name) {
		return getIntParameter(idContract, 0, 0, name);
	}
	
	public static int getContractNotNullIntParameter(long idContract,
			String name) {
		return getNotNullIntParameter(idContract, 0, 0, name);
	}
	
	public static Long getContractLongParameter(long idContract,
			String name) {
		return getLongParameter(idContract, 0, 0, name);
	}
	
	public static long getContractNotNullLongParameter(long idContract,
			String name) {
		return getNotNullLongParameter(idContract, 0, 0, name);
	}
	
	public static Double getContractDoubleParameter(long idContract,
			String name) {
		return getDoubleParameter(idContract, 0, 0, name);
	}
	
	public static double getContractNotNullDoubleParameter(long idContract,
			String name) {
		return getNotNullDoubleParameter(idContract, 0, 0, name);
	}
	
	// Récupère la valeur d'un paramètre de type "array"
	public static String[] getPlayerStringArrayParameter(long idContract, int idPlayer,
			String name) {
		return getStringArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static String[] getPlayerNotNullStringArrayParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullStringArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static boolean[] getPlayerBooleanArrayParameter(long idContract,
			int idPlayer, String name) {
		return getBooleanArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static boolean[] getPlayerNotNullBooleanArrayParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullBooleanArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static int[] getPlayerIntArrayParameter(long idContract,
			int idPlayer, String name) {
		return getIntArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static int[] getPlayerNotNullIntArrayParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullIntArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static long[] getPlayerLongArrayParameter(long idContract,
			int idPlayer, String name) {
		return getLongArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static long[] getPlayerNotNullLongArrayParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullLongArrayParameter(idContract, idPlayer, 0, name);
	}

	public static double[] getPlayerDoubleArrayParameter(long idContract,
			int idPlayer, String name) {
		return getDoubleArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static double[] getPlayerNotNullDoubleArrayParameter(long idContract,
			int idPlayer, String name) {
		return getNotNullDoubleArrayParameter(idContract, idPlayer, 0, name);
	}
	
	public static String[] getAllyStringArrayParameter(long idContract, int idAlly,
			String name) {
		return getStringArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static String[] getAllyNotNullStringArrayParameter(long idContract,
			int idAlly, String name) {
		return getNotNullStringArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static boolean[] getAllyBooleanArrayParameter(long idContract,
			int idAlly, String name) {
		return getBooleanArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static boolean[] getAllyNotNullBooleanArrayParameter(long idContract,
			int idAlly, String name) {
		return getNotNullBooleanArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static int[] getAllyIntArrayParameter(long idContract,
			int idAlly, String name) {
		return getIntArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static int[] getAllyNotNullIntArrayParameter(long idContract,
			int idAlly, String name) {
		return getNotNullIntArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static long[] getAllyLongArrayParameter(long idContract,
			int idAlly, String name) {
		return getLongArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static long[] getAllyNotNullLongArrayParameter(long idContract,
			int idAlly, String name) {
		return getNotNullLongArrayParameter(idContract, 0, idAlly, name);
	}

	public static double[] getAllyDoubleArrayParameter(long idContract,
			int idAlly, String name) {
		return getDoubleArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static double[] getAllyNotNullDoubleArrayParameter(long idContract,
			int idAlly, String name) {
		return getNotNullDoubleArrayParameter(idContract, 0, idAlly, name);
	}
	
	public static String[] getContractStringArrayParameter(long idContract,
			String name) {
		return getStringArrayParameter(idContract, 0, 0, name);
	}
	
	public static String[] getContractNotNullStringArrayParameter(long idContract,
			String name) {
		return getNotNullStringArrayParameter(idContract, 0, 0, name);
	}
	
	public static boolean[] getContractBooleanArrayParameter(long idContract,
			String name) {
		return getBooleanArrayParameter(idContract, 0, 0, name);
	}
	
	public static boolean[] getContractNotNullBooleanArrayParameter(long idContract,
			String name) {
		return getNotNullBooleanArrayParameter(idContract, 0, 0, name);
	}
	
	public static int[] getContractIntArrayParameter(long idContract,
			String name) {
		return getIntArrayParameter(idContract, 0, 0, name);
	}
	
	public static int[] getContractNotNullIntArrayParameter(long idContract,
			String name) {
		return getNotNullIntArrayParameter(idContract, 0, 0, name);
	}
	
	public static long[] getContractLongArrayParameter(long idContract,
			String name) {
		return getLongArrayParameter(idContract, 0, 0, name);
	}
	
	public static long[] getContractNotNullLongArrayParameter(long idContract,
			String name) {
		return getNotNullLongArrayParameter(idContract, 0, 0, name);
	}
	
	public static double[] getContractDoubleArrayParameter(long idContract,
			String name) {
		return getDoubleArrayParameter(idContract, 0, 0, name);
	}
	
	public static double[] getContractNotNullDoubleArrayParameter(long idContract,
			String name) {
		return getNotNullDoubleArrayParameter(idContract, 0, 0, name);
	}
	
	// Récupère la valeur d'un paramètre de type "map"
	public static String getPlayerStringMapParameter(long idContract, int idPlayer,
			String name, Object key) {
		return getStringMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static String getPlayerNotNullStringMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getNotNullStringMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static Boolean getPlayerBooleanMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getBooleanMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static boolean getPlayerNotNullBooleanMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getNotNullBooleanMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static Integer getPlayerIntMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getIntMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static int getPlayerNotNullIntMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getNotNullIntMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static Long getPlayerLongMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getLongMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static long getPlayerNotNullLongMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getNotNullLongMapParameter(idContract, idPlayer, 0, name, key);
	}

	public static Double getPlayerDoubleMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getDoubleMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static double getPlayerNotNullDoubleMapParameter(long idContract,
			int idPlayer, String name, Object key) {
		return getNotNullDoubleMapParameter(idContract, idPlayer, 0, name, key);
	}
	
	public static String getAllyStringMapParameter(long idContract, int idAlly,
			String name, Object key) {
		return getStringMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static String getAllyNotNullStringMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getNotNullStringMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static Boolean getAllyBooleanMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getBooleanMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static boolean getAllyNotNullBooleanMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getNotNullBooleanMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static Integer getAllyIntMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getIntMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static int getAllyNotNullIntMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getNotNullIntMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static Long getAllyLongMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getLongMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static long getAllyNotNullLongMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getNotNullLongMapParameter(idContract, 0, idAlly, name, key);
	}

	public static Double getAllyDoubleMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getDoubleMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static double getAllyNotNullDoubleMapParameter(long idContract,
			int idAlly, String name, Object key) {
		return getNotNullDoubleMapParameter(idContract, 0, idAlly, name, key);
	}
	
	public static String getContractStringMapParameter(long idContract,
			String name, Object key) {
		return getStringMapParameter(idContract, 0, 0, name, key);
	}
	
	public static String getContractNotNullStringMapParameter(long idContract,
			String name, Object key) {
		return getNotNullStringMapParameter(idContract, 0, 0, name, key);
	}
	
	public static Boolean getContractBooleanMapParameter(long idContract,
			String name, Object key) {
		return getBooleanMapParameter(idContract, 0, 0, name, key);
	}
	
	public static boolean getContractNotNullBooleanMapParameter(long idContract,
			String name, Object key) {
		return getNotNullBooleanMapParameter(idContract, 0, 0, name, key);
	}
	
	public static Integer getContractIntMapParameter(long idContract,
			String name, Object key) {
		return getIntMapParameter(idContract, 0, 0, name, key);
	}
	
	public static int getContractNotNullIntMapParameter(long idContract,
			String name, Object key) {
		return getNotNullIntMapParameter(idContract, 0, 0, name, key);
	}
	
	public static Long getContractLongMapParameter(long idContract,
			String name, Object key) {
		return getLongMapParameter(idContract, 0, 0, name, key);
	}
	
	public static long getContractNotNullLongMapParameter(long idContract,
			String name, Object key) {
		return getNotNullLongMapParameter(idContract, 0, 0, name, key);
	}
	
	public static Double getContractDoubleMapParameter(long idContract,
			String name, Object key) {
		return getDoubleMapParameter(idContract, 0, 0, name, key);
	}
	
	public static double getContractNotNullDoubleMapParameter(long idContract,
			String name, Object key) {
		return getNotNullDoubleMapParameter(idContract, 0, 0, name, key);
	}
	
	// Sauvegarde d'un paramètre de type "value"
	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, Object value) {
		storeParameter(idContract, idPlayer, 0, name, value);
	}
	
	public static void storeAllyParameter(long idContract, int idAlly,
			String name, Object value) {
		storeParameter(idContract, 0, idAlly, name, value);
	}
	
	public static void storeContractParameter(long idContract, String name, Object value) {
		storeParameter(idContract, 0, 0, name, value);
	}
	
	// Sauvegarde d'un paramètre de type "array"
	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, Object[] values) {
		storeParameter(idContract, idPlayer, 0, name, values);
	}

	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, boolean[] values) {
		storeParameter(idContract, idPlayer, 0, name, values);
	}
	
	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, int[] values) {
		storeParameter(idContract, idPlayer, 0, name, values);
	}
	
	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, long[] values) {
		storeParameter(idContract, idPlayer, 0, name, values);
	}
	
	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, double[] values) {
		storeParameter(idContract, idPlayer, 0, name, values);
	}
	
	public static void storeAllyParameter(long idContract, int idAlly,
			String name, Object[] values) {
		storeParameter(idContract, 0, idAlly, name, values);
	}

	public static void storeAllyParameter(long idContract, int idAlly,
			String name, boolean[] values) {
		storeParameter(idContract, 0, idAlly, name, values);
	}
	
	public static void storeAllyParameter(long idContract, int idAlly,
			String name, int[] values) {
		storeParameter(idContract, 0, idAlly, name, values);
	}
	
	public static void storeAllyParameter(long idContract, int idAlly,
			String name, long[] values) {
		storeParameter(idContract, 0, idAlly, name, values);
	}
	
	public static void storeAllyParameter(long idContract, int idAlly,
			String name, double[] values) {
		storeParameter(idContract, 0, idAlly, name, values);
	}
	
	public static void storeContractParameter(long idContract, String name,
			Object[] values) {
		storeParameter(idContract, 0, 0, name, values);
	}
	
	public static void storeContractParameter(long idContract, String name,
			boolean[] values) {
		storeParameter(idContract, 0, 0, name, values);
	}
	
	public static void storeContractParameter(long idContract, String name,
			int[] values) {
		storeParameter(idContract, 0, 0, name, values);
	}
	
	public static void storeContractParameter(long idContract, String name,
			long[] values) {
		storeParameter(idContract, 0, 0, name, values);
	}
	
	public static void storeContractParameter(long idContract, String name,
			double[] values) {
		storeParameter(idContract, 0, 0, name, values);
	}
	
	// Sauvegarde d'un paramètre de type "map"
	
	// Sauvegarde d'un paramètre de type "map"
	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, Map<?, ?> values) {
		storeParameter(idContract, idPlayer, 0, name, values);
	}
	
	public static void storeAllyParameter(long idContract, int idAlly,
			String name, Map<?, ?> values) {
		storeParameter(idContract, 0, idAlly, name, values);
	}
	
	
	public static void storeContractParameter(long idContract, String name,
			Map<?, ?> values) {
		storeParameter(idContract, 0, 0, name, values);
	}
	
	// Sauvegarde d'une clé pour un paramètre de type "map"
	public static void storePlayerParameter(long idContract, int idPlayer,
			String name, Object key, Object value) {
		storeParameter(idContract, idPlayer, 0, name, key, value);
	}
	
	public static void storeAllyParameter(long idContract, int idAlly,
			String name, Object key, Object value) {
		storeParameter(idContract, 0, idAlly, name, key, value);
	}
	
	public static void storeContractParameter(long idContract, String name,
			Object key, Object value) {
		storeParameter(idContract, 0, 0, name, key, value);
	}
	
	// Supprime un paramètre et ses valeurs
	public static void deletePlayerParameter(long idContract, int idPlayer, String name) {
		deleteParameter(idContract, idPlayer, 0, name);
	}
	
	public static void deleteAllyParameter(long idContract, int idAlly, String name) {
		deleteParameter(idContract, 0, idAlly, name);
	}
	
	public static void deleteContractParameter(long idContract, String name) {
		deleteParameter(idContract, 0, 0, name);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	private static ContractParameter createOrAlterParameter(long idContract,
			int idPlayer, int idAlly, String name, String type) {
		ContractParameter parameter = getParameterByName(
				idContract, idPlayer, idAlly, name);
		
		if (parameter != null) {
			alterParameterType(parameter, type);
		}
		else if(idAlly!=0)
		{
			parameter = new ContractParameter(idContract, 0, idAlly, name, type);
			parameter.save();
		}
		else{
			parameter = new ContractParameter(idContract, idPlayer, 0, name, type);
			parameter.save();
		}

		
		return parameter;
	}
	
	private static ContractParameter getParameterByName(long idContract,
			int idPlayer, int idAlly, String name) {
		List<ContractParameter> allParameters =
			DataAccess.getContractParametersByContract(idContract);
		
		for (ContractParameter parameter : allParameters)
			if (parameter.getIdPlayer() == idPlayer &&
					parameter.getIdAlly() == idAlly &&
					parameter.getName().equals(name)) {
				return parameter;
			}
		
		return null;
	}
	
	private static void alterParameterType(ContractParameter parameter,
			String type) {
		if (parameter != null && !parameter.getType().equals(type)) {
			List<ContractParameterValue> values =
				new ArrayList<ContractParameterValue>(parameter.getValues());
			
			for (ContractParameterValue value : values)
				value.delete();
			
			synchronized (parameter.getLock()) {
				parameter = DataAccess.getEditable(parameter);
				parameter.setType(type);
				parameter.save();
			}
		}
	}
	
	// Récupère la valeur d'un paramètre de type "value"
	private static String getStringParameter(long idContract, int idPlayer,
			int idAlly, String name) {
		ContractParameter parameter = getParameterByName(
				idContract, idPlayer, idAlly, name);
		
		if (parameter == null)
			return null;
		
		if (!parameter.getType().equals(ContractParameter.TYPE_VALUE))
			throw new IllegalArgumentException("Invalid parameter type.");
		
		List<ContractParameterValue> parameterValues = parameter.getValues();
		return parameterValues.get(0).getValue();
	}
	
	private static String getNotNullStringParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? "" : value;
	}
	
	private static Boolean getBooleanParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? null : Boolean.parseBoolean(value);
	}
	
	private static boolean getNotNullBooleanParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? false : Boolean.parseBoolean(value);
	}
	
	private static Integer getIntParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? null : Integer.parseInt(value);
	}
	
	private static int getNotNullIntParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? 0 : Integer.parseInt(value);
	}
	
	private static Long getLongParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? null : Long.parseLong(value);
	}
	
	private static long getNotNullLongParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? 0 : Long.parseLong(value);
	}

	private static Double getDoubleParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? null : Double.parseDouble(value);
	}
	
	private static double getNotNullDoubleParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String value = getStringParameter(idContract, idPlayer, idAlly, name);
		return value == null ? 0 : Double.parseDouble(value);
	}
	
	// Récupère la valeur d'un paramètre de type "array"
	private static String[] getStringArrayParameter(long idContract, int idPlayer,
			int idAlly, String name) {
		ContractParameter parameter = getParameterByName(idContract, idPlayer, idAlly, name);
		
		if (parameter == null)
			return null;
		
		if (!parameter.getType().equals(ContractParameter.TYPE_ARRAY))
			throw new IllegalArgumentException("Invalid parameter type.");
		
		List<ContractParameterValue> parameterValues = parameter.getValues();
		String[] values = new String[parameterValues.size()];
		
		for (ContractParameterValue parameterValue : parameterValues)
			values[Integer.parseInt(parameterValue.getKeyName())] = parameterValue.getValue();
		
		return values;
	}

	private static String[] getNotNullStringArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String[] value = getStringArrayParameter(idContract, idPlayer, idAlly, name);
		return value == null ? new String[0] : value;
	}
	
	private static boolean[] getBooleanArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String[] valuesStr = getStringArrayParameter(
			idContract, idPlayer, idAlly, name);
		
		if (valuesStr != null) {
			boolean[] values = new boolean[valuesStr.length];
			for (int i = 0; i < valuesStr.length; i++)
				values[i] = Boolean.parseBoolean(valuesStr[i]);
			return values;
		}
		
		return null;
	}
	
	private static boolean[] getNotNullBooleanArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		boolean[] value = getBooleanArrayParameter(idContract, idPlayer, idAlly, name);
		return value == null ? new boolean[0] : value;
	}
	
	private static int[] getIntArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String[] valuesStr = getStringArrayParameter(
			idContract, idPlayer, idAlly, name);
		
		if (valuesStr != null) {
			int[] values = new int[valuesStr.length];
			for (int i = 0; i < valuesStr.length; i++)
				values[i] = Integer.parseInt(valuesStr[i]);
			return values;
		}
		
		return null;
	}
	
	private static int[] getNotNullIntArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		int[] value = getIntArrayParameter(idContract, idPlayer, idAlly, name);
		return value == null ? new int[0] : value;
	}
	
	private static long[] getLongArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String[] valuesStr = getStringArrayParameter(
			idContract, idPlayer, idAlly, name);
		
		if (valuesStr != null) {
			long[] values = new long[valuesStr.length];
			for (int i = 0; i < valuesStr.length; i++)
				values[i] = Long.parseLong(valuesStr[i]);
			return values;
		}
		
		return null;
	}
	
	private static long[] getNotNullLongArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		long[] value = getLongArrayParameter(idContract, idPlayer, idAlly, name);
		return value == null ? new long[0] : value;
	}

	private static double[] getDoubleArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		String[] valuesStr = getStringArrayParameter(
			idContract, idPlayer, idAlly, name);
		
		if (valuesStr != null) {
			double[] values = new double[valuesStr.length];
			for (int i = 0; i < valuesStr.length; i++)
				values[i] = Double.parseDouble(valuesStr[i]);
			return values;
		}
		
		return null;
	}
	
	private static double[] getNotNullDoubleArrayParameter(long idContract,
			int idPlayer, int idAlly, String name) {
		double[] value = getDoubleArrayParameter(idContract, idPlayer, idAlly, name);
		return value == null ? new double[0] : value;
	}

	// Supprime un paramètre et ses valeurs
	private static void deleteParameter(long idContract, int idPlayer, int idAlly, String name) {
		List<ContractParameter> allParameters = new ArrayList<ContractParameter>(
				DataAccess.getContractParametersByContract(idContract));
		
		for (ContractParameter parameter : allParameters)
			if (parameter.getIdPlayer() == idPlayer &&
					parameter.getIdAlly() == idAlly &&
					parameter.getName().equals(name)) {
				parameter.delete();
				break;
			}
	}
	
	// Récupère la valeur d'un paramètre de type "map"
	private static String getStringMapParameter(long idContract, int idPlayer,
			int idAlly, String name, Object key) {
		ContractParameter parameter = getParameterByName(idContract, idPlayer, idAlly, name);
		
		if (parameter == null)
			return null;
		
		if (!parameter.getType().equals(ContractParameter.TYPE_MAP))
			throw new IllegalArgumentException("Invalid parameter type.");
		
		List<ContractParameterValue> parameterValues = parameter.getValues();
		
		for (ContractParameterValue parameterValue : parameterValues)
			if (parameterValue.getKeyName().equals(String.valueOf(key)))
				return parameterValue.getValue();
		
		return null;
	}
	
	private static String getNotNullStringMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? "" : value;
	}
	
	private static Boolean getBooleanMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? null : Boolean.parseBoolean(value);
	}
	
	private static boolean getNotNullBooleanMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? false : Boolean.parseBoolean(value);
	}
	
	private static Integer getIntMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? null : Integer.parseInt(value);
	}
	
	private static int getNotNullIntMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? 0 : Integer.parseInt(value);
	}
	
	private static Long getLongMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? null : Long.parseLong(value);
	}
	
	private static long getNotNullLongMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? 0 : Long.parseLong(value);
	}

	private static Double getDoubleMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? null : Double.parseDouble(value);
	}
	
	private static double getNotNullDoubleMapParameter(long idContract,
			int idPlayer, int idAlly, String name, Object key) {
		String value = getStringMapParameter(idContract, idPlayer, idAlly, name, key);
		return value == null ? 0 : Double.parseDouble(value);
	}

	// Sauvegarde d'un paramètre de type "value"
	private static void storeParameter(long idContract, int idPlayer, int idAlly,
			String name, Object value) {
		ContractParameter parameter = createOrAlterParameter(
				idContract, idPlayer, idAlly, name, ContractParameter.TYPE_VALUE);
		
		List<ContractParameterValue> parameterValues =
			new ArrayList<ContractParameterValue>(parameter.getValues());
		
		if (parameterValues.size() == 0) {
			// Crée une nouvelle valeur
			ContractParameterValue parameterValue =
				new ContractParameterValue(parameter.getId(), "", String.valueOf(value));
			parameterValue.save();
		} else {
			// Modifie la valeur existante
			ContractParameterValue parameterValue = parameterValues.get(0);
			
			synchronized (parameterValue.getLock()) {
				parameterValue = DataAccess.getEditable(parameterValue);
				parameterValue.setValue(String.valueOf(value));
				parameterValue.save();
			}
		}
	}
	
	// Sauvegarde d'un paramètre de type "array"
	private static void storeParameter(long idContract, int idPlayer, int idAlly,
			String name, Object[] values) {
		ContractParameter parameter = createOrAlterParameter(
			idContract, idPlayer, idAlly, name, ContractParameter.TYPE_ARRAY);
		
		List<ContractParameterValue> parameterValues =
			new ArrayList<ContractParameterValue>(parameter.getValues());
		
		if (parameterValues.size() != values.length) {
			// Crée des nouvelles valeurs
			for (ContractParameterValue parameterValue : parameterValues)
				parameterValue.delete();
			
			for (int i = 0; i < values.length; i++) {
				ContractParameterValue parameterValue =
					new ContractParameterValue(parameter.getId(),
						String.valueOf(i), String.valueOf(values[i]));
				parameterValue.save();
			}
		} else {
			// Modifie les valeurs existantes
			for (ContractParameterValue parameterValue : parameterValues) {
				synchronized (parameterValue.getLock()) {
					parameterValue = DataAccess.getEditable(parameterValue);
					parameterValue.setValue(String.valueOf(values[Integer.parseInt(
							parameterValue.getKeyName())]));
					parameterValue.save();
				}
			}
		}
	}
	
	private static void storeParameter(long idContract, int idPlayer, int idAlly,
			String name, boolean[] values) {
		String[] valuesStr = new String[values.length];
		for (int i = 0; i < values.length; i++)
			valuesStr[i] = String.valueOf(values[i]);
		storeParameter(idContract, idPlayer, idAlly, name, valuesStr);
	}
	
	private static void storeParameter(long idContract, int idPlayer, int idAlly,
			String name, int[] values) {
		String[] valuesStr = new String[values.length];
		for (int i = 0; i < values.length; i++)
			valuesStr[i] = String.valueOf(values[i]);
		storeParameter(idContract, idPlayer, idAlly, name, valuesStr);
	}
	
	private static void storeParameter(long idContract, int idPlayer, int idAlly,
			String name, long[] values) {
		String[] valuesStr = new String[values.length];
		for (int i = 0; i < values.length; i++)
			valuesStr[i] = String.valueOf(values[i]);
		storeParameter(idContract, idPlayer, idAlly, name, valuesStr);
	}
	
	private static void storeParameter(long idContract, int idPlayer, int idAlly,
			String name, double[] values) {
		String[] valuesStr = new String[values.length];
		for (int i = 0; i < values.length; i++)
			valuesStr[i] = String.valueOf(values[i]);
		storeParameter(idContract, idPlayer, idAlly, name, valuesStr);
	}
	
	// Sauvegarde d'une clé pour un paramètre de type "map"
	public static void storeParameter(long idContract, int idPlayer,
			int idAlly, String name, Object key, Object value) {
		ContractParameter parameter = createOrAlterParameter(
			idContract, idPlayer, idAlly, name, ContractParameter.TYPE_MAP);
		
		List<ContractParameterValue> parameterValues =
			new ArrayList<ContractParameterValue>(parameter.getValues());
		
		boolean found = false;
		for (ContractParameterValue parameterValue : parameterValues) {
			if (parameterValue.getKeyName().equals(String.valueOf(key))) {
				// Modifie la valeur existante
				synchronized (parameterValue.getLock()) {
					parameterValue = DataAccess.getEditable(parameterValue);
					parameterValue.setValue(String.valueOf(value));
					parameterValue.save();
				}
				found = true;
				break;
			}
		}
		
		// Crée une nouvelle valeur
		if (!found) {
			ContractParameterValue parameterValue =
				new ContractParameterValue(parameter.getId(),
					String.valueOf(key), String.valueOf(value));
			parameterValue.save();
		}
	}
}
