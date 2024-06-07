package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.cache.LoadingCache;

/*
 * data = children
 * findMatches = getChildren
 * VertexTest = Vertex, but I've ended up writing tests for the cache as well :) 
 */
@SpringBootTest
class VertexTest {

	// Data should remain in the Vertex class, but findMatches needs to see it, so I've added a setter in the cache class
	private List<String> data = new ArrayList<>(); // Should only be used within the findMatch method, all other calls should use the cache.getUnchecked or a wrapper which calls cache.getUnchecked

	private static final MyCache myCache = new MyCache(); // One cache for all Vertexes

	public VertexTest() {
		/*
		 * I originally had my cache setup in this constructor, however in the case of the Vertex class it seems like the wrong approach. 
		 * Even if the cache was declared final..
		 * 
		 * I can't have the cache setup in a static code block as then my children variable also needs to be static which doesn't make sense
		 * 
		 * I put the cache setup in another class, then it complained it couldn't see my data/children variable, and it doesn't look simple to pass a
		 * second argument to the caches load function. So instead, I'm setting the data via a setter method.
		 */
	}

	// @BeforeEach
	// clear everything

	@Test
	void cacheWorks() {
		LoadingCache<String, List<String>> cache = myCache.getCache();

		assertEquals(0, cache.size());

		data.add("Fish"); 
		data.add("Cat");
		data.add("Cat");
		data.add("Cat");
		data.add("Dog");
		data.add("Dog");
		data.add("Dog");
		myCache.setData(data); // Need to call / set this before every call to the cache 

		// Is Fish in the cache? currently no, but by calling getUnchecked it should be added
		List<String> expectedResults = new ArrayList<>();
		expectedResults.add("Cat");
		expectedResults.add("Cat");
		expectedResults.add("Cat");
		assertEquals(expectedResults, cache.getUnchecked("Cat"));  // Key (cat) Value (cat,cat,cat)
		assertEquals(1, cache.size());
	}

	@Test
	void cacheWorksWithNoData() throws ExecutionException {
		LoadingCache<String, List<String>> cache = myCache.getCache();

		assertEquals(0, cache.size());

		// Ensure data is empty here 
		myCache.setData(data); // Need to call / set this before every call to the cache 

		List<String> expectedResults = new ArrayList<>();
		assertEquals(expectedResults, cache.getUnchecked("Cat"));  // Try to find Cat in the cache, and expect an empty arraylist as it's value
		assertEquals(1, cache.size());
	}
}