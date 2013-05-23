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

package fr.fg.server.dao;

import java.util.List;

/*
* Toutes les classes qui mappent une table doivent étendre ProtectedData

* Chaque méthode setter de ces classes doit commencer par :

public void setXxx(....) {
  if (!isEditable())
    throwDataUneditableException();
  ...
}

(On lance une exception si l'objet est en mode "read-only")

* Utilisation :

Message m = new Message("hey", "cool", new Date().getTime() / 1000, 1, 2);
m.setContent("great"); // OK, l'objet n'est pas protégé (il n'a pas été enregistré dans les CAO/DAO)
DataAccess.create(m);
m.setContent("super"); // Erreur, lance une exception car l'objet a été enregistré dans les CAO/DAO et est passé en mode read-only)
m = DataAccess.getEditable(m); // Renvoie une copie éditable de m
m.setContent("woohoo"); // OK, l'objet est éditable
DataAccess.update(m);
m.setContent("yay"); // Erreur, l'objet a été enregistré et n'est pas éditable
DataAccess.delete(m);
m.setContent("woot"); // OK, l'objet n'est plus enregistré dans les CAO/DAO

De même :
Message m = DataAccess.getMessageById(10);
m.setContent("yo"); // Erreur, l'objet est en mode read-only
List<Message> msg = DataAccess.getMessagesByPlayer(1);
msg.get(0).setContent("yay"); // Idem

* Intéret : les CAO / DAO peuvent renvoyer les objets par référence en étant garantis qu'ils ne seront pas modifiés. S'ils n'étaient pas en mode read-only, on pourrait faire :

Message m = DataAccess.getMessageById(10);
m.setContent("yo");

Et le message dans le CAO aurait été modifié alors qu'il n'y pas eu de DataAccess.update(m).
On peut simplement obtenir un objet éditable en créant une copie de l'objet, sur laquelle on pourra travailler sans impacter les CAO/DAO :

Message m = DataAccess.getMessageById(10);
Message m2 = DataAccess.getEditable(m); // m2 est une copie éditable de m



// X une classe
// Attached = instance en cache, non éditable
// Detached = éditable

X a = new X();            // a detached
DA.save(c);               // a attached
X b = DA.getEditable(a);  // a attached, b detached
DA.save(b);               // a detached, b attached
DA.delete(b);             // a detached, b detached
DA.save(b);               // a detached, b attached
DA.delete(a);             // 1: réattache a => a attached, b detached ; 2: delete de l'objet => a detached, b detached


// Edition thread-safe un objet
X a = DA.getX(x);            // a attached

synchronized (a.getLock()) { // Pose un lock sur a, d'autres blocs qui veulent travailler sur a synchronisés avec synchronized (a) devront attendre la fin de la modification et récuperont ainsi la dernière version de l'objet
  X b = DA.getEditable(a);   // a attached, b detached
  b.setY(y);
  DA.save(b);                // a detached, b attached ; les prochains DA.getX(x) renveront b
}

// Parcours thread-safe d'une structure du DA
List<A> listA = DA.getBy(x);

synchronized (list) {
   for (A a : listA)
   	 do()
}

 */

public class PersistentData implements Cloneable {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private boolean editable;
	
	private PersistentData originalCopy;
	
	private Object lock;
	
	private long version;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public PersistentData() {
		this.version = 1;
		this.editable = true;
		this.lock = new Object(); // lock unique créé pour cet objet et toutes
								  // ses futures copies
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	// Renvoie le lock sur l'objet (partagé entre l'objet en cache et toutes
	// ses copies non attachés)
	public synchronized Object getLock() {
		return lock;
	}
	
	// REMIND jgottero renommer en isAttached
	protected boolean isEditable() {
		return editable;
	}
	
	protected void setEditable(boolean editable) {
		this.editable = editable;
		this.originalCopy = null;
	}
	
	protected PersistentData getOriginalCopy() {
		return originalCopy;
	}
	
	protected void setOriginalCopy(PersistentData originalCopy) {
		this.originalCopy = originalCopy;
	}
	
	protected void throwDataUneditableException() {
		throw new IllegalStateException("Data uneditable.");
	}
	
	public void save() {
		DataLayer.getInstance().save(this);
		version++;
	}
	
	public void delete() {
		DataLayer.getInstance().delete(this);
	}
	
	// Alias de delete
	public void remove() {
		delete();
	}
	
	public long getVersion() {
		return version;
	}
	
	public Object clone() throws CloneNotSupportedException {
		// Clone l'objet en cache (ie l'objet qui ne pointe pas sur une copie)
		if (originalCopy != null)
			return originalCopy.clone();
		
		Object clone = super.clone();
		
		((PersistentData) clone).setEditable(true);
		((PersistentData) clone).setOriginalCopy(
			originalCopy == null ? this : originalCopy);
		((PersistentData) clone).lock = lock; // Conserve toujours le même lock
		
		return clone;
	}
	
	public static <T extends PersistentData> T getObjectById(
			Class<T> t, String index, Object key) {
		return DataLayer.getInstance().getObjectById(t, index, key);
	}
	
	public static <T extends PersistentData> List<T> getObjectsById(
			Class<T> t, String index, Object key) {
		return DataLayer.getInstance().getObjectsById(t, index, key);
	}
	
	public static <T extends PersistentData> List<T> getAll(Class<T> t) {
		return DataLayer.getInstance().getAll(t);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
