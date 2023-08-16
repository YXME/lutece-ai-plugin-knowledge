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


 package fr.paris.lutece.plugins.knowledge.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;

import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Document objects
 */
public final class DocumentHome
{
    // Static variable pointed at the DAO instance
    private static IDocumentDAO _dao = SpringContextService.getBean( "knowledge.documentDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "knowledge" );
	private static IFileStoreServiceProvider _fileStoreService = FileService.getInstance( ).getFileStoreServiceProvider( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DocumentHome(  )
    {
    }

    /**
     * Create an instance of the document class
     * @param document The instance of the Document which contains the informations to store
     * @return The  instance of document which has been created with its primary key.
     */
    public static Document create( Document document )
    {
        _dao.insert( document, _plugin );

        return document;
    }

    /**
     * Update of the document which is specified in parameter
     * @param document The instance of the Document which contains the data to store
     * @return The instance of the  document which has been updated
     */
    public static Document update( Document document )
    {
        _dao.store( document, _plugin );

        return document;
    }

    /**
     * Remove the document whose identifier is specified in parameter
     * @param nKey The document Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a document whose identifier is specified in parameter
     * @param nKey The document primary key
     * @return an instance of Document
     */
    public static Optional<Document> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the document objects and returns them as a list
     * @return the list which contains the data of all the document objects
     */
    public static List<Document> getDocumentsList( )
    {
        return _dao.selectDocumentsList( _plugin );
    }
    
    /**
     * Load the id of all the document objects and returns them as a list
     * @return the list which contains the id of all the document objects
     */
    public static List<Integer> getIdDocumentsList( )
    {
        return _dao.selectIdDocumentsList( _plugin );
    }
    
    /**
     * Load the data of all the document objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the document objects
     */
    public static ReferenceList getDocumentsReferenceList( )
    {
        return _dao.selectDocumentsReferenceList( _plugin );
    }
    
    /**
     * Load the filteStoreService of the document objects and returns them as a IFileStoreServiceProvider
     * @return the filteStoreService of the document object
     */
	public static IFileStoreServiceProvider getFileStoreServiceProvider( )
    {
		return _fileStoreService;
    }
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Document> getDocumentsListByIds( List<Integer> listIds )
    {
        return _dao.selectDocumentsListByIds( _plugin, listIds );
    }

}

