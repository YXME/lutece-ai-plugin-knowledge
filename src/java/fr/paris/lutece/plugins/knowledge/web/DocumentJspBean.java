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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.plugins.knowledge.business.Document;
import fr.paris.lutece.plugins.knowledge.business.DocumentHome;
import fr.paris.lutece.plugins.knowledge.service.DocumentLoaderService;

/**
 * This class provides the user interface to manage Document features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDocuments.jsp", controllerPath = "jsp/admin/plugins/knowledge/", right = "KNOWLEDGE_MANAGEMENTDOC" )
public class DocumentJspBean extends AbstractPaginatorJspBean <Integer, Document>
{
	private static final long serialVersionUID = 1L;

	// Rights
	public static final String RIGHT_MANAGEDOCUMENTS = "KNOWLEDGE_MANAGEMENTDOC";
		
    // Templates
    private static final String TEMPLATE_MANAGE_DOCUMENTS = "/admin/plugins/knowledge/manage_documents.html";
    private static final String TEMPLATE_CREATE_DOCUMENT = "/admin/plugins/knowledge/create_document.html";
    private static final String TEMPLATE_MODIFY_DOCUMENT = "/admin/plugins/knowledge/modify_document.html";
    private static final String TEMPLATE_VIEW_CHAT = "/admin/plugins/knowledge/view_chat.html";
    
    // Parameters
    private static final String PARAMETER_ID_DOCUMENT = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DOCUMENTS = "knowledge.manage_documents.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DOCUMENT = "knowledge.modify_document.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DOCUMENT = "knowledge.create_document.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_VIEW_CHAT = "knowledge.view_chat.pageTitle";

    // Markers
    private static final String MARK_DOCUMENT_LIST = "document_list";
    private static final String MARK_DOCUMENT = "document";
    private static final String MARK_CHAT_DATA = "chatData";
    
    private static final String JSP_MANAGE_DOCUMENTS = "jsp/admin/plugins/knowledge/ManageDocuments.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DOCUMENT = "knowledge.message.confirmRemoveDocument";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "knowledge.model.entity.document.attribute.";

    // Views
    private static final String VIEW_MANAGE_DOCUMENTS = "manageDocuments";
    private static final String VIEW_CREATE_DOCUMENT = "createDocument";
    private static final String VIEW_MODIFY_DOCUMENT = "modifyDocument";
    
    private static final String VIEW_CHAT = "chat";

    // Actions
    private static final String ACTION_CREATE_DOCUMENT = "createDocument";
    private static final String ACTION_MODIFY_DOCUMENT = "modifyDocument";
    private static final String ACTION_REMOVE_DOCUMENT = "removeDocument";
    private static final String ACTION_CONFIRM_REMOVE_DOCUMENT = "confirmRemoveDocument";
    private static final String ACTION_LOAD_DOCUMENT = "loadDocument";
    
    private static final String ACTION_SEND_CHAT = "sendChat";

    // Infos
    private static final String INFO_DOCUMENT_CREATED = "knowledge.info.document.created";
    private static final String INFO_DOCUMENT_UPDATED = "knowledge.info.document.updated";
    private static final String INFO_DOCUMENT_REMOVED = "knowledge.info.document.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Document _document;
    private String _chatData;
    private List<Integer> _listIdDocuments;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DOCUMENTS, defaultView = true )
    public String getManageDocuments( HttpServletRequest request )
    {
        _document = null;
        
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdDocuments.isEmpty( ) )
        {
        	_listIdDocuments = DocumentHome.getIdDocumentsList(  );
        }
        
        Map<String, Object> model = getPaginatedListModel( request, MARK_DOCUMENT_LIST, _listIdDocuments, JSP_MANAGE_DOCUMENTS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DOCUMENTS, TEMPLATE_MANAGE_DOCUMENTS, model );
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Document> getItemsFromIds( List<Integer> listIds ) 
	{
		List<Document> listDocument = DocumentHome.getDocumentsListByIds( listIds );
        for (Document fil : listDocument)
        {
        	IFileStoreServiceProvider fileStoreService = DocumentHome.getFileStoreServiceProvider( );
			if ( fil.getDocumentData( ) != null )
			{
				File localFile = fileStoreService.getFileMetaData( fil.getDocumentData( ).getFileKey( ) );
				if ( localFile != null )
				{
					fil.setDocumentData( localFile );
					String strFileUrl = fileStoreService.getFileDownloadUrlBO( localFile.getFileKey( ) );
					fil.getDocumentData( ).setUrl( strFileUrl );
				}
			}
        }
		
		// keep original order
        return listDocument.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
		return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
	}
    
    /**
    * reset the _listIdDocuments list
    */
    public void resetListId( )
    {
    	_listIdDocuments = new ArrayList<>( );
    }

    /**
     * Returns the form to create a document
     *
     * @param request The Http request
     * @return the html code of the document form
     */
    @View( VIEW_CREATE_DOCUMENT )
    public String getCreateDocument( HttpServletRequest request )
    {
        _document = ( _document != null ) ? _document : new Document(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_DOCUMENT, _document );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_DOCUMENT ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DOCUMENT, TEMPLATE_CREATE_DOCUMENT, model );
    }

    /**
     * Process the data capture form of a new document
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_LOAD_DOCUMENT )
    public String doLoadDocument( HttpServletRequest request )
    {
    	  int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DOCUMENT ) );

          if ( _document == null || ( _document.getId(  ) != nId ) )
          {
              Optional<Document> optDocument = DocumentHome.findByPrimaryKey( nId );
              _document = optDocument.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
          }

         	IFileStoreServiceProvider fileStoreService = DocumentHome.getFileStoreServiceProvider( );
  		if ( _document.getDocumentData( ) != null && StringUtils.isNotEmpty( _document.getDocumentData( ).getFileKey( ) ) )
  		{
  			File localFile = fileStoreService.getFileMetaData( _document.getDocumentData( ).getFileKey( ) );
  			if ( localFile != null )
  			{
  				_document.setDocumentData( localFile );
  				String strFileUrl = fileStoreService.getFileDownloadUrlBO( localFile.getFileKey( ) );
  				_document.getDocumentData( ).setUrl( strFileUrl );
  			}
  		}

  		DocumentLoaderService.askQuestion("Combien");

        return redirectView( request, VIEW_MANAGE_DOCUMENTS );
    }
    
    /**
     * Process the data capture form of a new document
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_DOCUMENT )
    public String doCreateDocument( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _document, request, getLocale( ) );
        
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		IFileStoreServiceProvider fileStoreService = DocumentHome.getFileStoreServiceProvider( );
        FileItem document_data = multipartRequest.getFile( "document_data" );
        
        DocumentLoaderService.loadFile(document_data);
        
        if ( document_data != null && document_data.getSize( ) > 0 )
        {
            try
            {
                String strFileStoreKey = fileStoreService.storeFileItem( document_data );
                File localFile = new File( );
                localFile.setFileKey( strFileStoreKey );
                _document.setDocumentData( localFile );
            }
            catch (Exception e) 
            {
            	AppLogService.error( "Erreur de stockage du fichier", e );
                throw new AppException( "Erreur de stockage du fichier", e );
            }
        }

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_DOCUMENT ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _document, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DOCUMENT );
        }

        DocumentHome.create( _document );
        addInfo( INFO_DOCUMENT_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DOCUMENTS );
    }

    /**
     * Manages the removal form of a document whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DOCUMENT )
    public String getConfirmRemoveDocument( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DOCUMENT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DOCUMENT ) );
        url.addParameter( PARAMETER_ID_DOCUMENT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DOCUMENT, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a document
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage documents
     */
    @Action( ACTION_REMOVE_DOCUMENT )
    public String doRemoveDocument( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DOCUMENT ) );
        
        if ( _document == null || ( _document.getId(  ) != nId ) )
        {
            Optional<Document> optDocument = DocumentHome.findByPrimaryKey( nId );
            _document = optDocument.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }
        IFileStoreServiceProvider fileStoreService = DocumentHome.getFileStoreServiceProvider( );
	    if ( _document.getDocumentData( ) != null && StringUtils.isNotEmpty( _document.getDocumentData( ).getFileKey( ) ) )
	    {
        	fileStoreService.delete( _document.getDocumentData( ).getFileKey( ) );
        }
        
        DocumentHome.remove( nId );
        addInfo( INFO_DOCUMENT_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DOCUMENTS );
    }

    /**
     * Returns the form to update info about a document
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DOCUMENT )
    public String getModifyDocument( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DOCUMENT ) );

        if ( _document == null || ( _document.getId(  ) != nId ) )
        {
            Optional<Document> optDocument = DocumentHome.findByPrimaryKey( nId );
            _document = optDocument.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }

       	IFileStoreServiceProvider fileStoreService = DocumentHome.getFileStoreServiceProvider( );
		if ( _document.getDocumentData( ) != null && StringUtils.isNotEmpty( _document.getDocumentData( ).getFileKey( ) ) )
		{
			File localFile = fileStoreService.getFileMetaData( _document.getDocumentData( ).getFileKey( ) );
			if ( localFile != null )
			{
				_document.setDocumentData( localFile );
				String strFileUrl = fileStoreService.getFileDownloadUrlBO( localFile.getFileKey( ) );
				_document.getDocumentData( ).setUrl( strFileUrl );
			}
		}

        Map<String, Object> model = getModel(  );
        model.put( MARK_DOCUMENT, _document );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_DOCUMENT ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DOCUMENT, TEMPLATE_MODIFY_DOCUMENT, model );
    }

    /**
     * Process the change form of a document
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_DOCUMENT )
    public String doModifyDocument( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _document, request, getLocale( ) );
		
		IFileStoreServiceProvider fileStoreService = DocumentHome.getFileStoreServiceProvider( );
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        FileItem document_data = multipartRequest.getFile( "document_data" );
       	_document.getDocumentData( ).setFileKey( request.getParameter( "document_dataKey" ) );
      
        if ( document_data != null && document_data.getSize( ) > 0 )
        {	
            try
            {
            	fileStoreService.delete( _document.getDocumentData( ).getFileKey( ) );
                String strFileStoreKey = fileStoreService.storeFileItem( document_data );
                File localFile = new File( );
                localFile.setFileKey( strFileStoreKey );
                _document.setDocumentData( localFile );
            }
            catch (Exception e) 
            {
            	AppLogService.error( "Erreur de stockage du fichier", e );
                throw new AppException( "Erreur de stockage du fichier", e );
            }
        }
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_DOCUMENT ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _document, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DOCUMENT, PARAMETER_ID_DOCUMENT, _document.getId( ) );
        }

        DocumentHome.update( _document );
        addInfo( INFO_DOCUMENT_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DOCUMENTS );
    }
    
    @View( VIEW_CHAT )
    public String getChat( HttpServletRequest request )
    {
        if ( _chatData == null )
        {
        	_chatData = "";
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_CHAT_DATA, _chatData );
        return getPage( PROPERTY_PAGE_TITLE_VIEW_CHAT, TEMPLATE_VIEW_CHAT, model );
    }

    @Action( ACTION_SEND_CHAT )
    public String doSendChat( HttpServletRequest request )
    {   
        String question = request.getParameter("question");

		_chatData += "<br>Requête : " + question;
		_chatData += "<br>Réponse: " + DocumentLoaderService.askQuestion(question) + "<br>";

        return redirectView(request, VIEW_CHAT);
    }
}
