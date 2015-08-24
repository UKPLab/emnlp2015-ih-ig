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

package de.tudarmstadt.ukp.dkpro.argumentation.sequence.feature.rst;

import de.tudarmstadt.ukp.dkpro.argumentation.io.writer.ArgumentDumpWriter;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.rst.core.type.DiscourseRelation;
import de.tudarmstadt.ukp.dkpro.core.rst.core.type.EDU;
import de.tudarmstadt.ukp.dkpro.core.rst.core.type.RSTTreeNode;
import de.tudarmstadt.ukp.dkpro.tc.api.exception.TextClassificationException;
import de.tudarmstadt.ukp.dkpro.tc.api.features.Feature;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * @author Ivan Habernal
 */
public class RSTFeaturesExtractor
        extends JCasAnnotator_ImplBase
{
    protected List<Feature> extract(JCas jCas, Sentence sentence, String sentencePrefix)
            throws TextClassificationException
    {
        List<EDU> edus = JCasUtil.selectCovered(EDU.class, sentence);

        List<RSTTreeNode> rstTreeNodes = JCasUtil.selectCovered(RSTTreeNode.class, sentence);

        System.out.println(sentence.getCoveredText());
        RSTTreeNode node = findRSTTreeNodeSpanningOverSentence(sentence);
        System.out.println(node != null ? node.getClass() : "null");

        //        for (RSTTreeNode node : rstTreeNodes) {
        //            System.out.println(node);
        //        }
        System.out.println("----");

        return null;
    }

    /**
     * Returns a rst tree node with the very same boundaries as the sentence
     *
     * @param sentence sentence
     * @return rst node or null
     */
    protected RSTTreeNode findRSTTreeNodeSpanningOverSentence(Sentence sentence)
    {
        for (RSTTreeNode rstTreeNode : JCasUtil.selectCovered(RSTTreeNode.class, sentence)) {
            if (rstTreeNode.getBegin() == sentence.getBegin() && rstTreeNode.getEnd() == sentence
                    .getEnd()) {
                return rstTreeNode;
            }
        }

        return null;
    }

    protected List<RSTTreeNode> getPathFromRootToNode(RSTTreeNode targetNode, JCas jCas)
    {
        return null;
    }

    protected static void wrapRSTTreeNodeToTree(RSTTreeNode rstTreeNode)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rstTreeNode);

        if (rstTreeNode instanceof DiscourseRelation) {
            DiscourseRelation discourseRelation = (DiscourseRelation) rstTreeNode;

        }
    }

    protected static void wrapRSTTreeNodeToTreeRecursive(RSTTreeNode rstTreeNode,
            DefaultMutableTreeNode parentNode)
    {
        // add two children nodes
        DefaultMutableTreeNode arg1 = new DefaultMutableTreeNode(rstTreeNode);

        if (parentNode != null) {
            arg1.setParent(parentNode);
            parentNode.add(arg1);
        }

        //        XXX

    }

    public static void main(String[] args)
            throws Exception
    {
        SimplePipeline.runPipeline(CollectionReaderFactory
                        .createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
                                "/home/habi/research/argumentation-gold-annotated-sentiment-discourse-rst-full-bio/*.xmi"),
                AnalysisEngineFactory.createEngineDescription(ArgumentDumpWriter.class),
                AnalysisEngineFactory.createEngineDescription(RSTFeaturesExtractor.class));

    }

    @Override
    public void process(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
            try {
                extract(aJCas, sentence, "prefix");
            }
            catch (TextClassificationException e) {
                throw new AnalysisEngineProcessException(e);
            }
        }
    }
}
