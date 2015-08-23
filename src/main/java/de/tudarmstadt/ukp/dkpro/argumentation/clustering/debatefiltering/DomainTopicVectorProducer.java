/*
 * Copyright 2015
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
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

package de.tudarmstadt.ukp.dkpro.argumentation.clustering.debatefiltering;

import de.tudarmstadt.ukp.dkpro.argumentation.clustering.VectorUtils;
import de.tudarmstadt.ukp.dkpro.argumentation.clustering.topic.DocumentTopicAnnotator;
import de.tudarmstadt.ukp.dkpro.argumentation.types.WebArgumentMetadata;
import de.tudarmstadt.ukp.dkpro.core.mallet.type.TopicDistribution;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For each domain (e.g. HS, MS, etc.) creates an average topic vector (and stores into
 * a serialized Map(String, Vector) file). Requires documents annotated by
 * {@link DocumentTopicAnnotator}
 * <p/>
 * @author Ivan Habernal
 */
@Deprecated // didn't work
public class DomainTopicVectorProducer
        extends JCasConsumer_ImplBase
{
    /**
     * Where the serialized map will be stored
     */
    public static final String PARAM_OUTPUT_FILE = "outputFile";
    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true)
    File outputFile;

    Map<String, List<Vector>> domainVectorMap = new HashMap<>();

    @Override
    public void process(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        // get the domain
        WebArgumentMetadata metadata = JCasUtil.selectSingle(aJCas, WebArgumentMetadata.class);
        String domain = metadata.getTopic();

        // there must be exactly one topic annotation for the document
        TopicDistribution topicDistribution = JCasUtil.selectSingle(aJCas, TopicDistribution.class);

        // create vector (deep copy)
        double[] doubles = topicDistribution.getTopicProportions().toArray();
        DenseVector vector = new DenseVector(doubles, true);

        // and add to the map
        if (!domainVectorMap.containsKey(domain)) {
            domainVectorMap.put(domain, new ArrayList<Vector>());
        }
        domainVectorMap.get(domain).add(vector);
    }

    @Override
    public void collectionProcessComplete()
            throws AnalysisEngineProcessException
    {
        // now average the vectors
        Map<String, Vector> result = new HashMap<>();

        for (Map.Entry<String, List<Vector>> entry : domainVectorMap.entrySet()) {
            final String domain = entry.getKey();

            // get the average vector
            Vector averageVector = VectorUtils.averageVector(entry.getValue());

            result.put(domain, averageVector);
        }

        // and save them
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(outputFile));
            os.writeObject(result);
            IOUtils.closeQuietly(os);

            getLogger().info("Domain/Topicvector serialized into " + outputFile);
        }
        catch (IOException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
    }
}
