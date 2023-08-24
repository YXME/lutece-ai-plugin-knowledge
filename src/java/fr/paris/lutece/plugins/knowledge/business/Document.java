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

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import fr.paris.lutece.portal.business.file.File;

/**
 * This is the business class for the object Document
 */
public class Document implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty( message = "#i18n{knowledge.validation.document.DocumentName.notEmpty}" )
    private String _strDocumentName;

    private File _fileDocumentData;

    private int _nProjectId;

    private boolean _bIsEmbedding;

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the DocumentName
     * 
     * @return The DocumentName
     */
    public String getDocumentName( )
    {
        return _strDocumentName;
    }

    /**
     * Sets the DocumentName
     * 
     * @param strDocumentName
     *            The DocumentName
     */
    public void setDocumentName( String strDocumentName )
    {
        _strDocumentName = strDocumentName;
    }

    /**
     * Returns the DocumentData
     * 
     * @return The DocumentData
     */
    public File getDocumentData( )
    {
        return _fileDocumentData;
    }

    /**
     * Sets the DocumentData
     * 
     * @param fileDocumentData
     *            The DocumentData
     */
    public void setDocumentData( File fileDocumentData )
    {
        _fileDocumentData = fileDocumentData;
    }

    /**
     * Returns the ProjectId
     * 
     * @return The ProjectId
     */
    public int getProjectId( )
    {
        return _nProjectId;
    }

    /**
     * Sets the ProjectId
     * 
     * @param nProjectId
     *            The ProjectId
     */
    public void setProjectId( int nProjectId )
    {
        _nProjectId = nProjectId;
    }

    /**
     * Returns the IsEmbedding
     * 
     * @return The IsEmbedding
     */
    public boolean getIsEmbedding( )
    {
        return _bIsEmbedding;
    }

    /**
     * Sets the IsEmbedding
     * 
     * @param bIsEmbedding
     *            The IsEmbedding
     */
    public void setIsEmbedding( boolean bIsEmbedding )
    {
        _bIsEmbedding = bIsEmbedding;
    }

}
