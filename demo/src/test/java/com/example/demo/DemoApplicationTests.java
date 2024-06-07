package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@SpringBootTest
class DemoApplicationTests {

	private List<String> data = new ArrayList<>(); // Should only be used within the findMatch method, all other calls should use the cache.getUnchecked or a wrapper which calls cache.getUnchecked

	static CacheLoader<String, List<String>> loader; // <Key, ExpensiveGraph>
	static LoadingCache<String, List<String>> cache;

	public DemoApplicationTests() {
		data.add("Fish");
		data.add("Cat");
		data.add("Cat");
		data.add("Cat");
		data.add("Dog");
		data.add("Dog");
		data.add("Dog");

		// This is obv in a constructor, but it's static, is it fine? This would be in the Vertex constructor... seems sketch
		// I can't have it in a static code block as then my children variable also needs to be static
		// Can I put in another class?
		loader = new CacheLoader<String, List<String>>() {
			@Override
			public List<String> load(String key) {
				return findMatches(key); // this load method should call onto getChildren(type)
			}
		};

		cache = CacheBuilder.newBuilder().build(loader);
	}

	// @BeforeEach
	// clear everything

	@Test
	void cacheWorksAdvanced() {
		assertEquals(0, cache.size());

		// Is Fish in the cache? currently no, but by calling getUnchecked it should be added
		List<String> expectedResults = new ArrayList<>();
		expectedResults.add("Cat");
		expectedResults.add("Cat");
		expectedResults.add("Cat");
		assertEquals(expectedResults, cache.getUnchecked("Cat")); 

		// So hello now exists in the cache
		assertEquals(1, cache.size());
	}

	@Test
	void cacheWorksViaWrapper() {
		assertEquals(0, cache.size()); // use beforeach to clear this
		List<String> expectedResults = new ArrayList<>();
		expectedResults.add("Cat");
		expectedResults.add("Cat");
		expectedResults.add("Cat");
		getChildrenViaWrapper("Cat");
		assertEquals(1, cache.size());
	}

	// This would be the only method called externally
	private void getChildrenViaWrapper(String value){
		cache.getUnchecked(value);// which calls findMatches
	}

	// Mimicing implementation of getChildren(childType) where we search a data structure, and add any matching elements to an internal data structure (hashmap in real case)
	// And then we return that internal structure.
	private List<String> findMatches(String thingToFind){
		System.out.println("FindMatches called with: " + thingToFind);

		// The internal structure we add to (i.e HashMap.add())
		List<String> internalStructure = new ArrayList<>();
		
		// Another thought there, we are using forEach, with is syncronous, would it also be possible to parallelstream? perhaps use this if you dont add a cache. both together likely not sensible

		// For each element in data (children), if data contains it, add it to the internal structure
		internalStructure = data.stream().filter(element -> element.equals(thingToFind)).collect(Collectors.toList());
		
		// If we found any match return it 
		return internalStructure; // This return value gets saved in the cache
	}

}
