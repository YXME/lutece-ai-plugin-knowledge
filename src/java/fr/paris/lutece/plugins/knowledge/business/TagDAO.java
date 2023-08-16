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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for Tag objects
 */
public final class TagDAO implements ITagDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_tags, tag_name FROM knowledge_tag WHERE id_tags = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO knowledge_tag ( tag_name ) VALUES ( ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM knowledge_tag WHERE id_tags = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE knowledge_tag SET tag_name = ? WHERE id_tags = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_tags, tag_name FROM knowledge_tag";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_tags FROM knowledge_tag";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_tags, tag_name FROM knowledge_tag WHERE id_tags IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Tag tag, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , tag.getTagName( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                tag.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Tag> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        Tag tag = null;
	
	        if ( daoUtil.next( ) )
	        {
	            tag = new Tag();
	            int nIndex = 1;
	            
	            tag.setId( daoUtil.getInt( nIndex++ ) );
			    tag.setTagName( daoUtil.getString( nIndex ) );
	        }
	
	        return Optional.ofNullable( tag );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Tag tag, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
            	daoUtil.setString( nIndex++ , tag.getTagName( ) );
	        daoUtil.setInt( nIndex , tag.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Tag> selectTagsList( Plugin plugin )
    {
        List<Tag> tagList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            Tag tag = new Tag(  );
	            int nIndex = 1;
	            
	            tag.setId( daoUtil.getInt( nIndex++ ) );
			    tag.setTagName( daoUtil.getString( nIndex ) );
	
	            tagList.add( tag );
	        }
	
	        return tagList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTagsList( Plugin plugin )
    {
        List<Integer> tagList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            tagList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return tagList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectTagsReferenceList( Plugin plugin )
    {
        ReferenceList tagList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            tagList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return tagList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<Tag> selectTagsListByIds( Plugin plugin, List<Integer> listIds ) {
		List<Tag> tagList = new ArrayList<>(  );
		
		StringBuilder builder = new StringBuilder( );

		if ( !listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size(); i++ ) {
			    builder.append( "?," );
			}
	
			String placeHolders =  builder.deleteCharAt( builder.length( ) -1 ).toString( );
			String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";
			
			
	        try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
	        {
	        	int index = 1;
				for( Integer n : listIds ) {
					daoUtil.setInt(  index++, n ); 
				}
	        	
	        	daoUtil.executeQuery(  );
	        	while ( daoUtil.next(  ) )
		        {
		        	Tag tag = new Tag(  );
		            int nIndex = 1;
		            
		            tag.setId( daoUtil.getInt( nIndex++ ) );
				    tag.setTagName( daoUtil.getString( nIndex ) );
		            
		            tagList.add( tag );
		        }
		
		        daoUtil.free( );
		        
	        }
	    }
		return tagList;
		
	}
}
