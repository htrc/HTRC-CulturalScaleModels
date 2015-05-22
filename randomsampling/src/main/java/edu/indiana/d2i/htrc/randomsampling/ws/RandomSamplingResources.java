package edu.indiana.d2i.htrc.randomsampling.ws;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import edu.indiana.d2i.htrc.randomsampling.Configuration;
import edu.indiana.d2i.htrc.randomsampling.tree.CategoryTree;

@Path("/")
public class RandomSamplingResources {
	private static Logger logger = Logger.getLogger(RandomSamplingResources.class);
	
	@GET
	@Path("findidcount")
	public Response findIdCount(@QueryParam("category") String category,
		@Context HttpHeaders httpHeaders, @Context HttpServletRequest httpServletRequest) {
		if (category == null) {
			return Response.status(400).entity("category is null!").build();
		}		
		
		CategoryTree tree = CategoryTree.getSingelton(Configuration.getSingleton());
		try {
			int idcount = tree.findIdCount(category);
			logger.info(String.format("There are %d #id for %s", idcount, category));
			return Response.status(200).entity(String.valueOf(idcount)).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.status(404).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("randomsamples")
	public Response randomSampling(@QueryParam("category") String category,
		@QueryParam("number") String numStr,
		@Context HttpHeaders httpHeaders, @Context HttpServletRequest httpServletRequest) {
		if (category == null || numStr == null) {
			return Response.status(400).entity("Either category or number is null.").build();
		}
		
		int number = Integer.parseInt(numStr);
		CategoryTree tree = CategoryTree.getSingelton(Configuration.getSingleton());
		try {
			List<String> samples = tree.randomSampling(category, number);
			return Response.status(200).entity(StringUtils.join(samples, "|")).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.status(400).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("getid")
	public Response getIDs(@QueryParam("category") String category,
		@Context HttpHeaders httpHeaders, @Context HttpServletRequest httpServletRequest) {
		if (category == null) {
			return Response.status(400).entity("category is null!").build();
		}	
		
		CategoryTree tree = CategoryTree.getSingelton(Configuration.getSingleton());
		try {
			List<String> id = tree.getIDs(category);
			return Response.status(200).entity(StringUtils.join(id, "|")).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.status(404).entity(e.getMessage()).build();
		}
	}
}
