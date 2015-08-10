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

package clustering.topic;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 * Assigns topic distribution to each sentence
 *
 * @author XXX
 */
public class SentenceTopicAnnotator
        extends AbstractTopicAnnotator
{
    @Override
    public void process(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
            annotateWithTopicDistribution(aJCas, sentence.getBegin(), sentence.getEnd());
        }
    }
}
