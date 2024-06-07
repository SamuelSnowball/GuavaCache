package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class MyCache {
    static CacheLoader<String, List<String>> loader; // <Key, ExpensiveGraph>
	static LoadingCache<String, List<String>> cache;

	private List<String> data = new ArrayList<>(); // Passed in from Vertex class

    public MyCache(){
        loader = new CacheLoader<String, List<String>>() {
			@Override
			public List<String> load(String key) { // List<String> dataToSearchIn - how do I add this? can I?
				return findMatches(key, data); // this load method should call onto getChildren(type)
			}
		};

		cache = CacheBuilder.newBuilder().build(loader);
    }

    // Mimicing implementation of getChildren(childType) where we search a data structure, and add any matching elements to an internal data structure (hashmap in real case)
	// And then we return that internal structure.
	private List<String> findMatches(String thingToFind, List<String> dataToSearchIn){
		System.out.println("FindMatches called with: " + thingToFind);

		// The internal structure we add to (i.e HashMap.add())
		List<String> internalStructure = new ArrayList<>();
		
		// Another thought there, we are using forEach, with is syncronous, would it also be possible to parallelstream? perhaps use this if you dont add a cache. both together likely not sensible

		// For each element in data (children), if data contains it, add it to the internal structure
		internalStructure = data.stream().filter(element -> element.equals(thingToFind)).collect(Collectors.toList());
		
		// If we found any match return it 
		return internalStructure; // This return value gets saved in the cache
	}
    
    public void setData(List<String> data) {
        this.data = data;
    }

    public LoadingCache<String, List<String>> getCache() {
        return cache;
    }
}