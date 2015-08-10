/*
 * Copyright 2015 XXX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package clustering.debatefiltering;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import de.tudarmstadt.ukp.dkpro.core.tokit.ParagraphSplitter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import xxx.web.comments.clustering.topic.DocumentTopicAnnotator;
import xxx.web.comments.pipeline.ArktweetTokenizerFixed;
import xxx.web.comments.pipeline.FullDebateContentReader;
import xxx.web.comments.pipeline.SentenceOverlapSanityCheck;

/**
 * (c) 2015 XXX
 */
@Deprecated // doesn't work
public class DebateFilterByTopicSimilarity
{

    @Parameter(names = { "--ud", "--unlabeledDocumentsDir" },
            description = "Unlabeled documents (from CL)", required = true)
    String unlabeledDocumentsDir;

    @Parameter(names = { "--tm",
            "--topicModel" }, description = "Topic model on unlabeled documents (from CL)",
            required = true)
    String topicModel;

    @Parameter(names = { "--odtv", "--outputDomainTopicVector" },
            description = "Output domain/topic vector (.bin)", required = true)
    String outputDomainTopicVector;

    @Parameter(names = { "--dsd", "--debatesSourceDir" },
            description = "Debates source dir (with debates .xmi)", required = true)
    String debatesSourceDir;

    @Parameter(names = { "--mdod", "--mainDebatesOutputDir" },
            description = "Where the filtered and ranked debates will be stored", required = true)
    String mainDebatesOutputDir;

    public void createDomainTopicVectors()
            throws Exception
    {
        SimplePipeline.runPipeline(CollectionReaderFactory
                        .createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
                                this.unlabeledDocumentsDir, XmiReader.PARAM_PATTERNS,
                                XmiReader.INCLUDE_PREFIX + "*.xmi"),

                // paragraphs
                AnalysisEngineFactory.createEngineDescription(ParagraphSplitter.class),
                // tokenize web-texts
                AnalysisEngineFactory.createEngineDescription(ArktweetTokenizerFixed.class),
                // find sentences
                AnalysisEngineFactory.createEngineDescription(StanfordSegmenter.class,
                        StanfordSegmenter.PARAM_WRITE_TOKEN, false),
                // lemma
                AnalysisEngineFactory.createEngineDescription(StanfordLemmatizer.class),
                // sanity check
                AnalysisEngineFactory.createEngineDescription(SentenceOverlapSanityCheck.class),

                AnalysisEngineFactory.createEngineDescription(DocumentTopicAnnotator.class,
                        DocumentTopicAnnotator.PARAM_MODEL_LOCATION, this.topicModel),
                AnalysisEngineFactory.createEngineDescription(DomainTopicVectorProducer.class,
                        DomainTopicVectorProducer.PARAM_OUTPUT_FILE, this.outputDomainTopicVector));
    }

    public void rankDebates()
            throws Exception
    {
        SimplePipeline.runPipeline(
                // read debates
                CollectionReaderFactory.createReaderDescription(FullDebateContentReader.class,
                        FullDebateContentReader.PARAM_SOURCE_LOCATION, this.debatesSourceDir),

                // tokenize web-texts
                AnalysisEngineFactory.createEngineDescription(ArktweetTokenizerFixed.class),
                // find sentences
                AnalysisEngineFactory.createEngineDescription(StanfordSegmenter.class,
                        StanfordSegmenter.PARAM_WRITE_TOKEN, false),
                // lemma
                AnalysisEngineFactory.createEngineDescription(StanfordLemmatizer.class),
                // sanity check
                AnalysisEngineFactory.createEngineDescription(SentenceOverlapSanityCheck.class),

                // annotate debates with topics
                AnalysisEngineFactory.createEngineDescription(DocumentTopicAnnotator.class,
                        DocumentTopicAnnotator.PARAM_MODEL_LOCATION, this.topicModel),

                // and do the ranking and filtering
                AnalysisEngineFactory.createEngineDescription(DebateRanker.class,
                        DebateRanker.PARAM_DOMAIN_TOPIC_VECTOR_MODEL, this.outputDomainTopicVector,
                        DebateRanker.PARAM_MAIN_OUTPUT_DIR, this.mainDebatesOutputDir,
                        DebateRanker.PARAM_SOURCE_LOCATION, this.debatesSourceDir));
    }

    public static void main(String[] args)
            throws Exception
    {
        DebateFilterByTopicSimilarity filter = new DebateFilterByTopicSimilarity();

        JCommander jCommander = new JCommander(filter);
        try {
            jCommander.parse(args);
        }
        catch (ParameterException ex) {
            StringBuilder sb = new StringBuilder();
            jCommander.usage(sb);
            System.err.println(sb);
            throw ex;
        }

        /*
        Two documents are empty: 4636, 4657 (grep "sofaString=\"\"" *) - delete them manually
        in advance
         */
        // create domain/topic vectors
        //        filter.createDomainTopicVectors();

        filter.rankDebates();
    }
}
