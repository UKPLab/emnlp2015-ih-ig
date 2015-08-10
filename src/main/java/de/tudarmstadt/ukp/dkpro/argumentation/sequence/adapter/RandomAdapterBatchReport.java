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

package de.tudarmstadt.ukp.dkpro.argumentation.sequence.adapter;

import de.tudarmstadt.ukp.dkpro.argumentation.sequence.report.TokenLevelBatchCrossValidationReport;
import de.tudarmstadt.ukp.dkpro.lab.reporting.ReportBase;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.tc.svmhmm.SVMHMMAdapter;
import de.tudarmstadt.ukp.dkpro.tc.svmhmm.random.SVMHMMRandomTestTask;

/**
 * @author xxx
 */
@Deprecated // not used anymore
public class RandomAdapterBatchReport
        extends SVMHMMAdapter
{
    @Override public ExecutableTaskBase getTestTask()
    {
        return new SVMHMMRandomTestTask();
    }

    @Override public Class<? extends ReportBase> getBatchTrainTestReportClass()
    {
        return TokenLevelBatchCrossValidationReport.class;
    }
}