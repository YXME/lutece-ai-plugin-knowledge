/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.knowledge.web;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import java.util.List;
import java.io.IOException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.plugins.knowledge.business.Document;
import fr.paris.lutece.plugins.knowledge.business.DocumentHome;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import java.util.ArrayList;
import org.apache.commons.fileupload.FileItem;
import java.io.IOException;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import java.util.HashMap;
import java.util.Map;
/**
 * This is the business class test for the object Document
 */
public class DocumentJspBeanTest extends LuteceTestCase
{
    private static final String DOCUMENTNAME1 = "DocumentName1";
    private static final String DOCUMENTNAME2 = "DocumentName2";
	private static final File DOCUMENTDATA1 = new File( );
    private static final File DOCUMENTDATA2 = new File( );

public void testJspBeans(  ) throws AccessDeniedException, IOException
	{	
     	MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockServletConfig config = new MockServletConfig();

		//display admin Document management JSP
		DocumentJspBean jspbean = new DocumentJspBean();
		String html = jspbean.getManageDocuments( request );
		assertNotNull(html);

		//display admin Document creation JSP
		html = jspbean.getCreateDocument( request );
		assertNotNull(html);

		//action create Document
		request = new MockHttpServletRequest();

		response = new MockHttpServletResponse( );
		AdminUser adminUser = new AdminUser( );
		adminUser.setAccessCode( "admin" );
		
		Map<String, String [ ]> parameters = new HashMap<>( );
        parameters.put( "token", new String [ ] {
        		SecurityTokenService.getInstance( ).getToken( request, "createDocument" )
        } );
        parameters.put( "action", new String [ ] {
        		"createDocument"
        } );
        parameters.put( "document_name", new String [ ] {
        DOCUMENTNAME1
        } );
        
        Map<String, List<FileItem>> multipartFiles = new HashMap<>( );
        
        List<FileItem> items = new ArrayList<>( );
        
        FileItem document_data = new DiskFileItemFactory( ).createItem( "document_data", "text/plain", true, "document_data" );
        document_data.getOutputStream( ).write( "something".getBytes( ) );
        items.add( document_data );
        multipartFiles.put( "document_data", items );

        MultipartHttpServletRequest requestMultipart = new MultipartHttpServletRequest(request, multipartFiles, parameters);
		
		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(requestMultipart, adminUser);
			html = jspbean.processController( requestMultipart, response ); 
			
			
			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}

		//display modify Document JSP
		request = new MockHttpServletRequest();
        request.addParameter( "document_name" , DOCUMENTNAME1 );
		List<Integer> listIds = DocumentHome.getIdDocumentsList();
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new DocumentJspBean();
		
		assertNotNull( jspbean.getModifyDocument( request ) );	

		//action modify Document
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");
		
		parameters = new HashMap<>( );
        parameters.put( "action", new String [ ] {
        		"modifyDocument"
        } );
        parameters.put( "token", new String [ ] {
        		SecurityTokenService.getInstance( ).getToken( request, "modifyDocument" )
        } );
        parameters.put( "document_name", new String [ ] {
        DOCUMENTNAME2
        } );

        requestMultipart = new MultipartHttpServletRequest(request, new HashMap<>( ), parameters);

		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(requestMultipart, adminUser);
			html = jspbean.processController( requestMultipart, response );

			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}
		
		//get remove Document
		request = new MockHttpServletRequest();
        //request.setRequestURI("jsp/admin/plugins/example/ManageDocuments.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new DocumentJspBean();
		request.addParameter("action","confirmRemoveDocument");
		assertNotNull( jspbean.getModifyDocument( request ) );
				
		//do remove Document
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setRequestURI("jsp/admin/plugins/example/ManageDocumentts.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createDocument, qui est l'action par défaut
		request.addParameter("action","removeDocument");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeDocument" ));
		request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		request.setMethod("POST");
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");

		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			html = jspbean.processController( request, response ); 

			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}	
     
     }
}
