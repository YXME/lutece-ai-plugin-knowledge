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
import fr.paris.lutece.plugins.knowledge.business.Tag;
import fr.paris.lutece.plugins.knowledge.business.TagHome;
/**
 * This is the business class test for the object Tag
 */
public class TagJspBeanTest extends LuteceTestCase
{
    private static final String TAGNAME1 = "TagName1";
    private static final String TAGNAME2 = "TagName2";

public void testJspBeans(  ) throws AccessDeniedException, IOException
	{	
     	MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockServletConfig config = new MockServletConfig();

		//display admin Tag management JSP
		TagJspBean jspbean = new TagJspBean();
		String html = jspbean.getManageTags( request );
		assertNotNull(html);

		//display admin Tag creation JSP
		html = jspbean.getCreateTag( request );
		assertNotNull(html);

		//action create Tag
		request = new MockHttpServletRequest();

		response = new MockHttpServletResponse( );
		AdminUser adminUser = new AdminUser( );
		adminUser.setAccessCode( "admin" );
		
        
        request.addParameter( "tag_name" , TAGNAME1 );
		request.addParameter("action","createTag");
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createTag" ));
		request.setMethod( "POST" );
        
		
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

		//display modify Tag JSP
		request = new MockHttpServletRequest();
        request.addParameter( "tag_name" , TAGNAME1 );
		List<Integer> listIds = TagHome.getIdTagsList();
        assertTrue( !listIds.isEmpty( ) );
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new TagJspBean();
		
		assertNotNull( jspbean.getModifyTag( request ) );	

		//action modify Tag
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");
		
        request.addParameter( "tag_name" , TAGNAME2 );
		request.setRequestURI("jsp/admin/plugins/example/ManageTags.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createTag, qui est l'action par défaut
		request.addParameter("action","modifyTag");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyTag" ));

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
		
		//get remove Tag
		request = new MockHttpServletRequest();
        //request.setRequestURI("jsp/admin/plugins/example/ManageTags.jsp");
        request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new TagJspBean();
		request.addParameter("action","confirmRemoveTag");
		assertNotNull( jspbean.getModifyTag( request ) );
				
		//do remove Tag
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setRequestURI("jsp/admin/plugins/example/ManageTagts.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createTag, qui est l'action par défaut
		request.addParameter("action","removeTag");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeTag" ));
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
