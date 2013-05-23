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

package fr.fg.client.core;

import java.util.ArrayList;
import java.util.HashMap;

import fr.fg.client.data.ProductsData;

public class ProductsManager {
	// ------------------------------------------------------- CONSTANTES -- //
	// -------------------------------------------------------- ATTRIBUTS -- //
	
	private HashMap<Integer, Integer> productsMap;
	
	private ArrayList<ProductsListener> listeners;
	
	// ---------------------------------------------------- CONSTRUCTEURS -- //
	
	public ProductsManager() {
		this.productsMap = new HashMap<Integer, Integer>();
	}
	
	// --------------------------------------------------------- METHODES -- //
	
	public void setProducts(ProductsData products) {
		HashMap<Integer, Integer> productsMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < products.getProductsCount(); i++) {
			int product = products.getProductAt(i);
			if (productsMap.containsKey(product))
				productsMap.put(product, 1 + productsMap.get(product));
			else
				productsMap.put(product, 1);
		}
		
		this.productsMap = productsMap;
		
		if (listeners != null)
			for (ProductsListener listener : listeners)
				listener.onProductsChanged(productsMap);
	}
	
	public int getProductCount(int type) {
		return productsMap.containsKey(type) ? productsMap.get(type) : 0;
	}
	
	public HashMap<Integer, Integer> getProducts() {
		return productsMap;
	}
	
	public void addProductsListener(ProductsListener listener) {
		if (listeners == null)
			listeners = new ArrayList<ProductsListener>();
		listeners.add(listener);
	}
	
	public void removeProductsListener(ProductsListener listener) {
		if (listeners != null)
			listeners.remove(listener);
	}
	
	// ------------------------------------------------- METHODES PRIVEES -- //
	
	public static interface ProductsListener {
		// --------------------------------------------------- CONSTANTES -- //
		
		public void onProductsChanged(HashMap<Integer, Integer> newProducts);
		
		// ----------------------------------------------------- METHODES -- //
	}
}
