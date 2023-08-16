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
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.knowledge.business;

import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;

import fr.paris.lutece.portal.business.file.File;

/**
 * This is the business class test for the object Document
 */
public class DocumentBusinessTest extends LuteceTestCase
{
    private static final String DOCUMENTNAME1 = "DocumentName1";
    private static final String DOCUMENTNAME2 = "DocumentName2";
	private static final File DOCUMENTDATA1 = new File( );
    private static final File DOCUMENTDATA2 = new File( );

	/**
	* test Document
	*/
    public void testBusiness(  )
    {
        // Initialize an object
        Document document = new Document();
        document.setDocumentName( DOCUMENTNAME1 );
        document.setDocumentData( DOCUMENTDATA1 );

        // Create test
        DocumentHome.create( document );
        Optional<Document> optDocumentStored = DocumentHome.findByPrimaryKey( document.getId( ) );
        Document documentStored = optDocumentStored.orElse( new Document ( ) );
        assertEquals( documentStored.getDocumentName( ) , document.getDocumentName( ) );
        assertEquals( documentStored.getDocumentData( ).getFileKey( ) , document.getDocumentData( ).getFileKey( ) );

        // Update test
        document.setDocumentName( DOCUMENTNAME2 );
        document.setDocumentData( DOCUMENTDATA2 );
        DocumentHome.update( document );
        optDocumentStored = DocumentHome.findByPrimaryKey( document.getId( ) );
        documentStored = optDocumentStored.orElse( new Document ( ) );
        
        assertEquals( documentStored.getDocumentName( ) , document.getDocumentName( ) );
        assertEquals( documentStored.getDocumentData( ).getFileKey( ) , document.getDocumentData( ).getFileKey( ) );

        // List test
        DocumentHome.getDocumentsList( );

        // Delete test
        DocumentHome.remove( document.getId( ) );
        optDocumentStored = DocumentHome.findByPrimaryKey( document.getId( ) );
        documentStored = optDocumentStored.orElse( null );
        assertNull( documentStored );
        
    }
    
    
     

}