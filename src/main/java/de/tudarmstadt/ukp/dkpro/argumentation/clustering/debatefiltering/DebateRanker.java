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

package de.tudarmstadt.ukp.dkpro.argumentation.clustering.debatefiltering;

import de.tudarmstadt.ukp.dkpro.argumentation.clustering.VectorUtils;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.mallet.type.TopicDistribution;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * (c) 2015 XXX
 */
@Deprecated // doesn't work
public class DebateRanker
        extends JCasConsumer_ImplBase
{
    public static final String PARAM_MAIN_OUTPUT_DIR = "mainOutputDir";
    @ConfigurationParameter(name = PARAM_MAIN_OUTPUT_DIR)
    File mainOutputDir;

    public static final String PARAM_SOURCE_LOCATION = "sourceDebateDir";
    @ConfigurationParameter(name = PARAM_SOURCE_LOCATION)
    File sourceDebateDir;

    public static final String PARAM_DOMAIN_TOPIC_VECTOR_MODEL = "domainTopicVectorModel";
    @ConfigurationParameter(name = PARAM_DOMAIN_TOPIC_VECTOR_MODEL)
    File domainTopicVectorModel;

    // loaded domain/topic vector map
    Map<String, Vector> domainTopicVectorMap;

    // Jensen–Shannon divergences of debates to domains
    Map<String, List<Double>> domainDivergencesMap = new TreeMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException
    {
        super.initialize(context);

        // load domain topic map
        try {
            if (!domainTopicVectorModel.exists()) {
                throw new IOException(domainTopicVectorModel + " not found");
            }
            this.domainTopicVectorMap = (Map<String, Vector>) new ObjectInputStream(
                    new FileInputStream(domainTopicVectorModel)).readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            throw new ResourceInitializationException(e);
        }

        // initialize the map
        for (String domain : domainTopicVectorMap.keySet()) {
            domainDivergencesMap.put(domain, new ArrayList<Double>());
        }
    }

    @Override
    public void process(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        // document topic vector - there must be exactly one topic annotation for the document
        TopicDistribution topicDistribution = JCasUtil.selectSingle(aJCas, TopicDistribution.class);

        // create vector (deep copy)
        double[] doubles = topicDistribution.getTopicProportions().toArray();

        // distances (JS-divergences) to all domains
        Map<String, Double> distancesToDomains = new HashMap<>();

        for (String domain : this.domainTopicVectorMap.keySet()) {
            // existing domain vector
            Vector averageTopicVectorForDomain = this.domainTopicVectorMap.get(domain);

            // compute JS divergence
            //            double jsDivergence = Maths.jensenShannonDivergence(doubles,
            //                    VectorUtils.toDoubleArray(averageTopicVectorForDomain));
            double cosineSimilarity = VectorUtils
                    .cosineSimilarity(new DenseVector(doubles), averageTopicVectorForDomain);

            //            distancesToDomains.put(domain, jsDivergence);
            distancesToDomains.put(domain, cosineSimilarity);
        }

        System.out.println(distancesToDomains);

        // find the "closest" domain (minimum divergence)
        Map.Entry<String, Double> max = Collections
                .max(distancesToDomains.entrySet(), new Comparator<Map.Entry<String, Double>>()
                {
                    public int compare(Map.Entry<String, Double> entry1,
                            Map.Entry<String, Double> entry2)
                    {
                        return entry1.getValue().compareTo(entry2.getValue());
                    }
                });

        // TMP add to the list
        domainDivergencesMap.get(max.getKey()).add(max.getValue());

        // copy the debate to the appropriate output folder
        try {
            copyDebate(aJCas, max.getKey(), max.getValue());
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    private void copyDebate(JCas aJCas, String domain, Double divergence)
            throws IOException
    {
        // binarize divergence
        double[] thresholds = new double[] { 0.95, 0.9, 0.85, 0.8 };
        for (double d : thresholds) {
            if (divergence >= d) {
                copy(aJCas, domain, d);
            }
        }

    }

    private void copy(JCas aJCas, String domain, double v)
            throws IOException
    {
        // create output dir
        File outputDir = new File(this.mainOutputDir, domain + "/" + Double.toString(v));
        outputDir.mkdirs();

        // localize input dir
        String url = DocumentMetaData.get(aJCas).getDocumentId();

        String fileName = url.replaceAll("http://", "").replaceAll("/", "___") + ".xml";
        File sourceFile = new File(this.sourceDebateDir, fileName);

        // and copy
        FileUtils.copyFileToDirectory(sourceFile, outputDir);

        getLogger().info("Copying " + sourceFile + " to " + outputDir);
    }

    @Override
    public void collectionProcessComplete()
            throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();

        for (String domain : this.domainDivergencesMap.keySet()) {
            System.out.println(domain);
            List<Double> doubles = this.domainDivergencesMap.get(domain);
            Collections.sort(doubles);
            System.out.println(doubles);
        }
    }

}
