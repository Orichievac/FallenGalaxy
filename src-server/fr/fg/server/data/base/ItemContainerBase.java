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

package fr.fg.server.data.base;

import fr.fg.server.dao.PersistentData;

public class ItemContainerBase extends PersistentData {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private long id;
	private int item0Type;
	private long item0Id;
	private long item0IdStructure;
	private double item0Count;
	private int item1Type;
	private long item1Id;
	private long item1IdStructure;
	private double item1Count;
	private int item2Type;
	private long item2Id;
	private long item2IdStructure;
	private double item2Count;
	private int item3Type;
	private long item3Id;
	private long item3IdStructure;
	private double item3Count;
	private int idFleet;
	private long idStructure;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	// --------------------------------------------------------- METHODES -- //
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.id = id;
	}
	
	public int getItem0Type() {
		return item0Type;
	}
	
	public void setItem0Type(int item0Type) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item0Type = item0Type;
	}
	
	public long getItem0Id() {
		return item0Id;
	}
	
	public void setItem0Id(long item0Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item0Id = item0Id;
	}
	
	public long getItem0IdStructure() {
		return item0IdStructure;
	}
	
	public void setItem0IdStructure(long item0IdStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item0IdStructure = item0IdStructure;
	}
	
	public double getItem0Count() {
		return item0Count;
	}
	
	public void setItem0Count(double item0Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item0Count = item0Count;
	}
	
	public int getItem1Type() {
		return item1Type;
	}
	
	public void setItem1Type(int item1Type) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item1Type = item1Type;
	}
	
	public long getItem1Id() {
		return item1Id;
	}
	
	public void setItem1Id(long item1Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item1Id = item1Id;
	}
	
	public long getItem1IdStructure() {
		return item1IdStructure;
	}
	
	public void setItem1IdStructure(long item1IdStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item1IdStructure = item1IdStructure;
	}
	
	public double getItem1Count() {
		return item1Count;
	}
	
	public void setItem1Count(double item1Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item1Count = item1Count;
	}
	
	public int getItem2Type() {
		return item2Type;
	}
	
	public void setItem2Type(int item2Type) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item2Type = item2Type;
	}
	
	public long getItem2Id() {
		return item2Id;
	}
	
	public void setItem2Id(long item2Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item2Id = item2Id;
	}
	
	public long getItem2IdStructure() {
		return item2IdStructure;
	}
	
	public void setItem2IdStructure(long item2IdStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item2IdStructure = item2IdStructure;
	}
	
	public double getItem2Count() {
		return item2Count;
	}
	
	public void setItem2Count(double item2Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item2Count = item2Count;
	}
	
	public int getItem3Type() {
		return item3Type;
	}
	
	public void setItem3Type(int item3Type) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item3Type = item3Type;
	}
	
	public long getItem3Id() {
		return item3Id;
	}
	
	public void setItem3Id(long item3Id) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item3Id = item3Id;
	}
	
	public long getItem3IdStructure() {
		return item3IdStructure;
	}
	
	public void setItem3IdStructure(long item3IdStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item3IdStructure = item3IdStructure;
	}
	
	public double getItem3Count() {
		return item3Count;
	}
	
	public void setItem3Count(double item3Count) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.item3Count = item3Count;
	}
	
	public int getIdFleet() {
		return idFleet;
	}
	
	public void setIdFleet(int idFleet) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idFleet = idFleet;
	}
	
	public long getIdStructure() {
		return idStructure;
	}
	
	public void setIdStructure(long idStructure) {
		if (!isEditable())
			throwDataUneditableException();
		
		this.idStructure = idStructure;
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
}
