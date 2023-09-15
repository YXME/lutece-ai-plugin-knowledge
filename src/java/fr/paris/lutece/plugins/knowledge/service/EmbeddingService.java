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
package fr.paris.lutece.plugins.knowledge.service;

import dev.langchain4j.data.document.*;
import dev.langchain4j.data.document.parser.*;
import dev.langchain4j.data.document.splitter.*;
import dev.langchain4j.data.embedding.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.*;
import dev.langchain4j.model.openai.*;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.inmemory.*;
import fr.paris.lutece.plugins.knowledge.business.DocumentHome;
import fr.paris.lutece.plugins.knowledge.business.EmbeddingHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.formula.functions.T;

import static dev.langchain4j.model.openai.OpenAiModelName.*;
import static java.time.Duration.*;

public class EmbeddingService
{

    private static IFileStoreServiceProvider fileStoreService = DocumentHome.getFileStoreServiceProvider( );
    private static final String API_KEY = AppPropertiesService.getProperty( "knowledge.apiKey" );
    private static final Map<Integer, EmbeddingStore<TextSegment>> embeddingStores = new HashMap<>( );
    private static final EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder( ).apiKey( API_KEY ).modelName( TEXT_EMBEDDING_ADA_002 )
            .timeout( ofSeconds( 600 ) ).logRequests( true ).logResponses( true ).build( );

    /**
     * Stores the files for the given project ID.
     * 
     * @param projectId
     *            The ID of the project.
     */
    public static void storeFiles( int projectId )
    {
        DocumentService.getDocumentsList( projectId ).forEach( document -> storeFile( document, projectId ) );
    }

    /**
     * Stores a file.
     * 
     * @param document
     *            The document to store.
     * @param projectId
     *            The ID of the project.
     */
    public static void storeFile( fr.paris.lutece.plugins.knowledge.business.Document document, int projectId )
    {
        String fileKey = document.getDocumentData( ).getFileKey( );
        List<fr.paris.lutece.plugins.knowledge.business.Embedding> knowledgeEmbeddingList = EmbeddingHome.getEmbeddingsListByFileId( fileKey );
        EmbeddingStore<TextSegment> projectEmbeddingStore = embeddingStores.computeIfAbsent( projectId, k -> new InMemoryEmbeddingStore<>( ) );

        if ( !knowledgeEmbeddingList.isEmpty( ) )
        {
            addExistingEmbeddings( document, knowledgeEmbeddingList, projectEmbeddingStore );
        }
        else
        {
            processAndStoreFile( document, fileKey, projectEmbeddingStore, projectId );
        }
    }

    /**
     * Adds existing embeddings to the project embedding store.
     * 
     * @param document
     *            The document.
     * @param knowledgeEmbeddingList
     *            The list of existing embeddings.
     * @param projectEmbeddingStore
     *            The project's embedding store.
     */
    private static void addExistingEmbeddings( fr.paris.lutece.plugins.knowledge.business.Document document,
            List<fr.paris.lutece.plugins.knowledge.business.Embedding> knowledgeEmbeddingList, EmbeddingStore<TextSegment> projectEmbeddingStore )
    {
        List<Embedding> embeddings = knowledgeEmbeddingList.stream( )
                .map( knowledgeEmbedding -> Embedding.from( vectorStringToFloatArray( knowledgeEmbedding.getVectors( ) ) ) ).collect( Collectors.toList( ) );

        List<TextSegment> segments = knowledgeEmbeddingList.stream( ).map( knowledgeEmbedding -> {
            Metadata metadata = Metadata.from( "file_name", document.getDocumentName( ) );
            metadata.add( "embedding_id", knowledgeEmbedding.getId( ) );
            metadata.add( "embedding_text", knowledgeEmbedding.getTextSegment( ) );
            return new TextSegment( knowledgeEmbedding.getTextSegment( ), metadata );
        } ).collect( Collectors.toList( ) );

        projectEmbeddingStore.addAll( embeddings, segments );
    }

    /**
     * Processes and stores a file.
     * 
     * @param document
     *            The document to process and store.
     * @param fileKey
     *            The key of the file.
     * @param projectEmbeddingStore
     *            The project's embedding store.
     * @param projectId
     *            The ID of the project.
     */
    private static void processAndStoreFile( fr.paris.lutece.plugins.knowledge.business.Document document, String fileKey,
            EmbeddingStore<TextSegment> projectEmbeddingStore, int projectId )
    {
        File file = fileStoreService.getFileMetaData( document.getDocumentData( ).getFileKey( ) );
        try ( InputStream inputStream = fileStoreService.getInputStream( file.getFileKey( ) ) )
        {
        	Document document4j;
        	String extension = file.getTitle().substring(file.getTitle().lastIndexOf("."), file.getTitle().length());

        	switch(extension) {
        		case ".pdf" :
        			document4j= new PdfDocumentParser( ).parse( inputStream );
        			break;
        		case ".docx":
        			document4j= new MsOfficeDocumentParser( DocumentType.DOC ).parse( inputStream );
        			break;
        		case ".pptx":
        			document4j= new MsOfficeDocumentParser( DocumentType.PPT ).parse( inputStream );
        			break;
        		case ".xlsx":
        			document4j= new MsOfficeDocumentParser( DocumentType.XLS ).parse( inputStream );
        			break;
        		default:
        			document4j= new TextDocumentParser( DocumentType.TXT ).parse( inputStream );
        			break;
        	}

            List<TextSegment> segments = new DocumentByLineSplitter(100).split( document4j );
            List<Embedding> embeddings = embeddingModel.embedAll( segments );

            List<fr.paris.lutece.plugins.knowledge.business.Embedding> knowledgeEmbeddings = new ArrayList<>( );
            for ( int i = 0; i < embeddings.size( ); i++ )
            {
                Embedding embedding = embeddings.get( i );
                TextSegment segment = segments.get( i );
                knowledgeEmbeddings.add( createKnowledgeEmbedding( fileKey, projectId, embedding, segment ) );
            }

            addExistingEmbeddings( document, knowledgeEmbeddings, projectEmbeddingStore );

        }
        catch( Exception e )
        {
            AppLogService.error( "Error while processing file " + fileKey, e );
        }
    }

    /**
     * Creates a knowledge embedding.
     * 
     * @param fileKey
     *            The key of the file.
     * @param projectId
     *            The ID of the project.
     * @param embedding
     *            The embedding.
     * @param segment
     *            The text segment.
     * @return The created knowledge embedding.
     */
    private static fr.paris.lutece.plugins.knowledge.business.Embedding createKnowledgeEmbedding( String fileKey, int projectId, Embedding embedding,
            TextSegment segment )
    {
        fr.paris.lutece.plugins.knowledge.business.Embedding knowledgeEmbedding = new fr.paris.lutece.plugins.knowledge.business.Embedding( );
        knowledgeEmbedding.setFileId( fileKey );
        knowledgeEmbedding.setProjectId( projectId );
        knowledgeEmbedding.setVectors( Arrays.toString( embedding.vector( ) ) );
        knowledgeEmbedding.setTextSegment( segment.text( ) );

        return EmbeddingHome.create( knowledgeEmbedding );
    }

    /**
     * Converts a string representation of a vector into a float array.
     * 
     * @param vectorString
     *            The string representation of the vector.
     * @return The float array.
     */
    private static float [ ] vectorStringToFloatArray( String vectorString )
    {
        vectorString = vectorString.substring( 1, vectorString.length( ) - 1 );
        String [ ] vectorArray = vectorString.split( "," );
        float [ ] floatArray = new float [ vectorArray.length];
        for ( int i = 0; i < vectorArray.length; i++ )
        {
            floatArray [i] = Float.parseFloat( vectorArray [i].trim( ) );
        }
        return floatArray;
    }

    /**
     * Gets the embedding store by project ID.
     * 
     * @param projectId
     *            The ID of the project.
     * @return The embedding store.
     */
    public static EmbeddingStore<TextSegment> getEmbeddingStoreByProjectId( int projectId )
    {
        return embeddingStores.get( projectId );
    }

    /**
     * Gets the embedding model.
     * 
     * @return The embedding model.
     */
    public static EmbeddingModel getEmbeddingModel( )
    {
        return embeddingModel;
    }

    /**
     * Gets the embedding sources for the relevant embeddings.
     * 
     * @param relevantEmbeddings
     *            The relevant embeddings.
     * @return The list of embedding sources.
     */
    public static List<fr.paris.lutece.plugins.knowledge.business.Embedding> getEmbeddingSources( List<EmbeddingMatch<TextSegment>> relevantEmbeddings )
    {
        return EmbeddingHome.getEmbeddingsListByIds( relevantEmbeddings.stream( )
                .map( match -> Integer.parseInt( match.embedded( ).metadata( ).get( "embedding_id" ) ) ).collect( Collectors.toList( ) ) );
    }

}
