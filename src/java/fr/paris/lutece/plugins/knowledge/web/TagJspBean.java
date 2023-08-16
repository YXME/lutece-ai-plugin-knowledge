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
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
 	
 
package fr.paris.lutece.plugins.knowledge.web;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.plugins.knowledge.business.Tag;
import fr.paris.lutece.plugins.knowledge.business.TagHome;

/**
 * This class provides the user interface to manage Tag features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTags.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENTTAGS" )
public class TagJspBean extends AbstractPaginatorJspBean <Integer, Tag>
{

	// Rights
	public static final String RIGHT_MANAGETAGS = "KNOWLEDGE_MANAGEMENTTAGS";
		
    // Templates
    private static final String TEMPLATE_MANAGE_TAGS = "/admin/plugins/knowledge/manage_tags.html";
    private static final String TEMPLATE_CREATE_TAG = "/admin/plugins/knowledge/create_tag.html";
    private static final String TEMPLATE_MODIFY_TAG = "/admin/plugins/knowledge/modify_tag.html";

    // Parameters
    private static final String PARAMETER_ID_TAG = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TAGS = "knowledge.manage_tags.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TAG = "knowledge.modify_tag.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TAG = "knowledge.create_tag.pageTitle";

    // Markers
    private static final String MARK_TAG_LIST = "tag_list";
    private static final String MARK_TAG = "tag";

    private static final String JSP_MANAGE_TAGS = "jsp/admin/plugins/knowledge/ManageTags.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TAG = "knowledge.message.confirmRemoveTag";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.tag.attribute.";

    // Views
    private static final String VIEW_MANAGE_TAGS = "manageTags";
    private static final String VIEW_CREATE_TAG = "createTag";
    private static final String VIEW_MODIFY_TAG = "modifyTag";

    // Actions
    private static final String ACTION_CREATE_TAG = "createTag";
    private static final String ACTION_MODIFY_TAG = "modifyTag";
    private static final String ACTION_REMOVE_TAG = "removeTag";
    private static final String ACTION_CONFIRM_REMOVE_TAG = "confirmRemoveTag";

    // Infos
    private static final String INFO_TAG_CREATED = "knowledge.info.tag.created";
    private static final String INFO_TAG_UPDATED = "knowledge.info.tag.updated";
    private static final String INFO_TAG_REMOVED = "knowledge.info.tag.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Tag _tag;
    private List<Integer> _listIdTags;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TAGS, defaultView = true )
    public String getManageTags( HttpServletRequest request )
    {
        _tag = null;
        
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdTags.isEmpty( ) )
        {
        	_listIdTags = TagHome.getIdTagsList(  );
        }
        
        Map<String, Object> model = getPaginatedListModel( request, MARK_TAG_LIST, _listIdTags, JSP_MANAGE_TAGS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TAGS, TEMPLATE_MANAGE_TAGS, model );
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Tag> getItemsFromIds( List<Integer> listIds ) 
	{
		List<Tag> listTag = TagHome.getTagsListByIds( listIds );
		
		// keep original order
        return listTag.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
		return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
	}
    
    /**
    * reset the _listIdTags list
    */
    public void resetListId( )
    {
    	_listIdTags = new ArrayList<>( );
    }

    /**
     * Returns the form to create a tag
     *
     * @param request The Http request
     * @return the html code of the tag form
     */
    @View( VIEW_CREATE_TAG )
    public String getCreateTag( HttpServletRequest request )
    {
        _tag = ( _tag != null ) ? _tag : new Tag(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TAG, _tag );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_TAG ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TAG, TEMPLATE_CREATE_TAG, model );
    }

    /**
     * Process the data capture form of a new tag
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_TAG )
    public String doCreateTag( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _tag, request, getLocale( ) );
        

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_TAG ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _tag, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TAG );
        }

        TagHome.create( _tag );
        addInfo( INFO_TAG_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TAGS );
    }

    /**
     * Manages the removal form of a tag whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TAG )
    public String getConfirmRemoveTag( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TAG ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TAG ) );
        url.addParameter( PARAMETER_ID_TAG, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TAG, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a tag
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage tags
     */
    @Action( ACTION_REMOVE_TAG )
    public String doRemoveTag( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TAG ) );
        
        
        TagHome.remove( nId );
        addInfo( INFO_TAG_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TAGS );
    }

    /**
     * Returns the form to update info about a tag
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TAG )
    public String getModifyTag( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TAG ) );

        if ( _tag == null || ( _tag.getId(  ) != nId ) )
        {
            Optional<Tag> optTag = TagHome.findByPrimaryKey( nId );
            _tag = optTag.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_TAG, _tag );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_TAG ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TAG, TEMPLATE_MODIFY_TAG, model );
    }

    /**
     * Process the change form of a tag
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_TAG )
    public String doModifyTag( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _tag, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_TAG ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _tag, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TAG, PARAMETER_ID_TAG, _tag.getId( ) );
        }

        TagHome.update( _tag );
        addInfo( INFO_TAG_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_TAGS );
    }
}
