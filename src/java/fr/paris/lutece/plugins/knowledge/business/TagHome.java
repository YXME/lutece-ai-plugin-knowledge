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


import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Tag objects
 */
public final class TagHome
{
    // Static variable pointed at the DAO instance
    private static ITagDAO _dao = SpringContextService.getBean( "knowledge.tagDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "knowledge" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TagHome(  )
    {
    }

    /**
     * Create an instance of the tag class
     * @param tag The instance of the Tag which contains the informations to store
     * @return The  instance of tag which has been created with its primary key.
     */
    public static Tag create( Tag tag )
    {
        _dao.insert( tag, _plugin );

        return tag;
    }

    /**
     * Update of the tag which is specified in parameter
     * @param tag The instance of the Tag which contains the data to store
     * @return The instance of the  tag which has been updated
     */
    public static Tag update( Tag tag )
    {
        _dao.store( tag, _plugin );

        return tag;
    }

    /**
     * Remove the tag whose identifier is specified in parameter
     * @param nKey The tag Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a tag whose identifier is specified in parameter
     * @param nKey The tag primary key
     * @return an instance of Tag
     */
    public static Optional<Tag> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the tag objects and returns them as a list
     * @return the list which contains the data of all the tag objects
     */
    public static List<Tag> getTagsList( )
    {
        return _dao.selectTagsList( _plugin );
    }
    
    /**
     * Load the id of all the tag objects and returns them as a list
     * @return the list which contains the id of all the tag objects
     */
    public static List<Integer> getIdTagsList( )
    {
        return _dao.selectIdTagsList( _plugin );
    }
    
    /**
     * Load the data of all the tag objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the tag objects
     */
    public static ReferenceList getTagsReferenceList( )
    {
        return _dao.selectTagsReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Tag> getTagsListByIds( List<Integer> listIds )
    {
        return _dao.selectTagsListByIds( _plugin, listIds );
    }

}

