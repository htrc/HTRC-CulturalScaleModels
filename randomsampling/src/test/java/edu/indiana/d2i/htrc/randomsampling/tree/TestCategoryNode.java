package edu.indiana.d2i.htrc.randomsampling.tree;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.indiana.d2i.htrc.randomsampling.Configuration;
import edu.indiana.d2i.htrc.randomsampling.exceptions.SampleNumberTooLarge;

public class TestCategoryNode extends CategoryNode {
	private static CategoryNode root;
	
	@BeforeClass
	public static void beforeClass() {
		Configuration config = Configuration.getSingleton();
		config.setString(Configuration.PropertyNames.VOLUME_CALLNO, "./eng-QH-callno");
		config.setString(Configuration.PropertyNames.LOCC_RDF, "./conf/lcco.rdf");
		CategoryTree tree = CategoryTree.getSingelton(config);

		root = tree.root();
	}
	
	@Test
	public void testCategory() {		
		Assert.assertTrue(CategoryNode.validateCategoryString("QH366"));
		Assert.assertTrue(CategoryNode.validateCategoryString("QH43.23"));
		Assert.assertFalse(CategoryNode.validateCategoryString("U56 no.82"));
	}
	
	/**	test exact match */
	@Test
	public void testChildren1() {		
		// non-leaf nodes
		Assert.assertEquals(20, root.childrenCount());
		Assert.assertEquals(13, root.findParent("Q").childrenCount());
		Assert.assertEquals(2, root.findParent("QH1-278.5").childrenCount());
		Assert.assertEquals(7, root.findParent("QH301-705.5").childrenCount());
		Assert.assertEquals(10, root.findParent("GA101-1776").childrenCount());
		Assert.assertEquals(2, root.findParent("KBM1-4855").childrenCount());
		Assert.assertEquals(7, root.findParent("KZD1002-6715").childrenCount());
		Assert.assertEquals(6, root.findParent("E-F").childrenCount());
		Assert.assertEquals(13, root.findParent("F1-975").childrenCount());
		Assert.assertEquals(1, root.findParent("E184-185.98").childrenCount());
		
		// leaf nodes
		Assert.assertEquals(0, root.findParent("KZ1345-1369").childrenCount());
		Assert.assertEquals(0, root.findParent("F1401-1419").childrenCount());	
		Assert.assertEquals(0, root.findParent("GA109.5").childrenCount());
	}
	
  /** test fuzzy match */
	@Test
	public void testChildren2() {		
		// on leaf nodes
		Assert.assertEquals("QH1-199.5", root.findParent("QH5").toString());
		Assert.assertEquals("QH1-199.5", root.findParent("QH1").toString());
//		Assert.assertEquals("F1561-1577", root.findParent("F1570").toString());	
		
		// on non leaf nodes
		Assert.assertEquals("QH301-705.5", root.findParent("QH332").toString());
	}
	
	/** test prefix match */
	@Test
	public void testChildren3() {	
		List<CategoryNode> nodes1 = root.findParentsByPrefix("QH");
		Assert.assertEquals(2, nodes1.size());
	}
	
	@Test
	public void testGetSampleNums() throws SampleNumberTooLarge {
		int[] counts = {5};
		int[] samples = CategoryNode.getSampleNums(1, counts);
		System.out.println(Arrays.toString(samples));
	}
}
