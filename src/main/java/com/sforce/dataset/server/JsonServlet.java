/*
 * Copyright (c) 2014, salesforce.com, inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 *    following disclaimer.
 *  
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 *    the following disclaimer in the documentation and/or other materials provided with the distribution. 
 *    
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or 
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED 
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.sforce.dataset.server;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sforce.dataset.DatasetUtilConstants;
import com.sforce.dataset.util.DatasetDownloader;
import com.sforce.dataset.util.XmdUploader;

@MultipartConfig 
public class JsonServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
//	private static final ThreadPoolExecutor executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), Executors.defaultThreadFactory());
//	private static List<FileUploadRequest> files = new LinkedList<FileUploadRequest>();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			String type = request.getParameter("type");
			String datasetAlias = request.getParameter("datasetAlias");
			String jsonString = request.getParameter("jsonString");
				    
			if(type==null || type.trim().isEmpty())
			{
				throw new IllegalArgumentException("type is a required param");
			}

			if(datasetAlias==null || datasetAlias.trim().isEmpty())
			{
				throw new IllegalArgumentException("datasetAlias is a required param");
			}

			String orgId = DatasetUtilServer.partnerConnection.getUserInfo().getOrganizationId();
			if(type.equalsIgnoreCase("xmd"))
			{
				File dataDir = DatasetUtilConstants.getDataDir(orgId);
				File datasetDir = new File(dataDir,datasetAlias);
				FileUtils.forceMkdir(datasetDir);
				ObjectMapper mapper = new ObjectMapper();	
				@SuppressWarnings("rawtypes")
				Map xmdObject =  mapper.readValue(jsonString, Map.class);
				File outfile = new File(datasetDir,"user.xmd.json");
				mapper.writerWithDefaultPrettyPrinter().writeValue(outfile , xmdObject);				
				XmdUploader.uploadXmd(outfile.getAbsolutePath(), datasetAlias, DatasetUtilServer.partnerConnection);
			}else
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request {"+type+"}");
			}
			
			ResponseStatus status = new ResponseStatus("success",null);
			response.setContentType("application/json");
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), status);
		}catch(Throwable t)
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request {"+t.toString()+"}");
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try 
		{		
					String type = request.getParameter("type");
					if(type==null || type.trim().isEmpty())
					{
						throw new IllegalArgumentException("type is a required param");
					}
					
					String datasetAlias = request.getParameter("datasetAlias");
					if(datasetAlias==null || datasetAlias.trim().isEmpty())
					{
						throw new IllegalArgumentException("datasetAlias is a required param");
					}

					
					if(type.equalsIgnoreCase("xmd"))
					{
						String xmd = DatasetDownloader.getXMD(datasetAlias, DatasetUtilServer.partnerConnection);
					    response.setContentType("application/json");
				    	ObjectMapper mapper = new ObjectMapper();
						@SuppressWarnings("rawtypes")
						Map xmdObject =  mapper.readValue(xmd, Map.class);
				    	mapper.writeValue(response.getOutputStream(), xmdObject);
//					}else if(type.equalsIgnoreCase("metadataJson"))
//					{
//						List<DatasetType> datasets = DatasetUtils.listDatasets(DatasetUtilServer.partnerConnection);
//						DatasetType def = new DatasetType();
//						def.name = "";
//						def._alias = "";
//						def._type = "edgemart";
//						datasets.add(0, def);
//					    response.setContentType("application/json");
//				    	ObjectMapper mapper = new ObjectMapper();
//				    	mapper.writeValue(response.getOutputStream(), datasets);
					}else
					{
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request {"+type+"}");
					}
		 }catch (Throwable t) {
			 	t.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request {"+t.toString()+"}");
		 }
	}
}
