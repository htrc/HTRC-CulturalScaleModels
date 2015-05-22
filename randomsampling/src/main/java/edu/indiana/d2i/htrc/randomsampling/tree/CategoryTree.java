package edu.indiana.d2i.htrc.randomsampling.tree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.indiana.d2i.htrc.randomsampling.Configuration;
import edu.indiana.d2i.htrc.randomsampling.exceptions.NoCategoryFound;
import edu.indiana.d2i.htrc.randomsampling.exceptions.SampleNumberTooLarge;

public class CategoryTree {
	private static Logger logger = Logger.getLogger(CategoryTree.class);
	private static CategoryTree instance;
	private final CategoryNode root;
	
	private Property rootProperty;
	private Property childProperty;
	private void dfs(CategoryNode parent, Model model, Resource resource, Property property) {
		Selector selector = new SimpleSelector(resource, property, (Object)null);
		StmtIterator iter = model.listStatements(selector);
		
		String uri = resource.getURI();
		while (iter.hasNext()) {
			String str = iter.next().getObject().toString();					
			if (str.startsWith("http://inkdroid.org/lcco/") && !str.equals(uri)) {				
				String category = str.substring(str.lastIndexOf("/")+1);
				category = category.replaceAll("\\(|\\)|\\s", "");
				
//				System.out.println(category);
				
				CategoryNode child = parent.addChild(category);				
				dfs(child, model, model.createResource(str), childProperty);
			}
		}	
	}
	
	private void loadCategory(String path) {
		try (InputStream in = new FileInputStream(path)) {			
			// read the RDF/XML file
			Model model = ModelFactory.createDefaultModel().read(in, null);
			
			// build the tree (dfs)
			rootProperty = model.createProperty("http://www.w3.org/2008/05/skos#hasTopConcept");
			childProperty = model.createProperty("http://www.w3.org/2008/05/skos#narrower");
			dfs(root, model, model.createResource("http://inkdroid.org/lcco/"), rootProperty);			
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	// TODO: check line format
	private void loadVolumeId(String path) {
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = null;
			// <callno, <id1, id2, ...>>
			Map<String, List<String>> categoryMapping = new HashMap<String, List<String>>();
			
			// an example line is like: 
			// uc2.ark:/13960/t57d2rr1p        ['QH81 .W68', 'QH81 .W56']
			int total = 0;
			int discard = 0;
			while ((line = reader.readLine()) != null) {
				int pivot = line.indexOf("[");
				int last = line.indexOf("]");
				String id = line.substring(0, pivot).trim();
				String[] callnums = line.substring(pivot+1, last).trim().split(",");
				
				boolean validCategory = false;
				for (String callnum : callnums) {				
					if (callnum.length() <= 1) continue;					
					String categoryStr = callnum.substring(1, callnum.length()-1).split("\\.")[0].trim();
					if (CategoryNode.validateCategoryString(categoryStr)) {
						if (!categoryMapping.containsKey(categoryStr)) {
							categoryMapping.put(categoryStr, new ArrayList<String>());
						}
						categoryMapping.get(categoryStr).add(id);
						validCategory = true;
					}
				}
				
				total++;
				if (!validCategory) discard++;
			}			
			
			int idCnt = 0;
			for (Map.Entry<String, List<String>> entry : categoryMapping.entrySet()) {
				String categoryStr = entry.getKey();
				List<String> idlist = entry.getValue();				
				CategoryNode node = root.findParent(categoryStr);
				if (node != null) {
					for (String id : idlist) node.addId(id);
					idCnt += idlist.size();
				} else {
					logger.warn(categoryStr + " is not found!");
				}
			}
			
			idCntInserted = idCnt;
			logger.info(String.format("Total #id: %d, #id discarded: %d, #id inserted: %d", total, discard, idCnt));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private CategoryTree(Configuration conf) {
		// build the structure
		root = new CategoryNode(" ");
		loadCategory(conf.getString(Configuration.PropertyNames.LOCC_RDF));
		logger.info("Category loading is finished!");
		
		// load the volume id
		loadVolumeId(conf.getString(Configuration.PropertyNames.VOLUME_CALLNO));
		logger.info("Volume id loading is finished!");
	}
	
  // unit test purpose only
	protected int idCount() {
		return root.idCount();
	}
	
	// unit test purpose only
	protected CategoryNode root() {
		return root;
	}
	
	// unit test purpose only
	protected int idCntInserted = 0;
	
	public synchronized static CategoryTree getSingelton(Configuration conf) {
		if (instance == null) {
			instance = new CategoryTree(conf);
		}		
		return instance;
	}
	
	public int findIdCount(String categoryStr) throws NoCategoryFound {
		CategoryNode node = root.findParent(categoryStr);  
		if (node == null) {
			List<CategoryNode> nodes = root.findParentsByPrefix(categoryStr);
			if (nodes == null) {
				throw new NoCategoryFound(categoryStr + " is not found!");
			} else {
				int sum = 0;
				for (CategoryNode item : nodes) {
					sum += item.idCount();
				}
				return sum;
			}			
		} else {
			return node.idCount();
		}
	}
	
	public List<String> randomSampling(String categoryStr, int number) 
		throws NoCategoryFound, SampleNumberTooLarge {
		CategoryNode node = root.findParent(categoryStr);
		if (node == null) {
			List<CategoryNode> nodes = root.findParentsByPrefix(categoryStr);
			if (nodes == null) {
				throw new NoCategoryFound(categoryStr + " is not found!");
			} else {
				int[] counts = new int[nodes.size()];
				for (int i = 0; i < nodes.size(); i++) counts[i] = nodes.get(i).idCount();
				int[] samples = CategoryNode.getSampleNums(number, counts);
				List<String> res = new ArrayList<String>();
				for (int i = 0; i < counts.length; i++) {
					res.addAll(nodes.get(i).samples(samples[i]));
				}
				return res;
			}
		} else {
			return node.samples(number);
		}
	}
	
	public List<String> getIDs(String categoryStr) throws NoCategoryFound {
		CategoryNode node = root.findParent(categoryStr);  
		if (node == null) {
			List<CategoryNode> nodes = root.findParentsByPrefix(categoryStr);
			if (nodes == null) {
				throw new NoCategoryFound(categoryStr + " is not found!");
			} else {
				List<String> res = new ArrayList<String>();
				for (CategoryNode item : nodes) {
					res.addAll(item.getAllIDs());
				}
				return res;
			}			
		} else {
			return node.getAllIDs();
		}
	}
}
