# emnlp2015
WORK IN PROGRESS; to be finished before Sept 17, 2015
Source code, data, and supplementary materials to the EMNLP2015 article



Exploiting Debate Portals for Argumentation Mining in User-Generated Web Discourse
EMNLP 2015
Readme v0.1

= Requirements

- Java 1.7 and higher, Maven
- tested on 64-bit Linux versions
- we recommend 32 GB RAM

= Installation

- Install all dependencies from ./dependencies/3rd-party to your local maven repository

	$mvn install

in
	dependencies/3rd-party/de.tudarmstadt.ukp.dkpro.core.api.discourse-asl
	dependencies/3rd-party/de.tudarmstadt.ukp.dkpro.core.api.sentiment-asl
	dependencies/3rd-party/dkpro-argumentation
	dependencies/3rd-party/dkpro-core-rst

and the same for dependencies/xxx.web.comments/

- Compile the main package in main/

	$mvn package

= Running the experiments

	$cd main/target
	$LC_ALL=en_US.UTF-8 java -XX:+UseSerialGC -Xmx32g -Xms32g -cp lib/*:xxx.argumentation.sequence-0.0.3-SNAPSHOT.jar \
	xxx.argumentation.sequence.evaluation.ArgumentSequenceLabelingEvaluation \
	--featureSet fs0 \
	--corpusPath data/argumentation-gold-annotated-sentiment-discourse-rst-full-bio-embeddings-xxx \
	--outputPath /tmp \
	--paramE 0 \
	--paramT 1 \
	--scenario cd \
	--clusters a100,s1000

- Parameter description:

    --cl, --clusters
       Which clusters? Comma-delimited, e.g., s100,a500
  * --corpusPath, --c
       Corpus path with XMI files
  * --featureSet, --fs
       Feature set name (e.g., fs0, fs0fs1fs2, fs3fs4, ...)
  * --outputPath, --o
       Main output path (folder)
    --paramE, --e
       Parameter e for SVMHMM
       Default: 0
    --paramT, --t
       Parameter T for SVMHMM
       Default: 1
  * --scenario, --s
       Evaluation scenario (cv = cross-validation, cd = cross domain, id = in domain)

= [Optional] Creating own clusters

1. Preprocessing pipeline - from debates in XML to UIMA XMI files
	$cd dependencies/xxx.web.comments

- Run xxx.web.comments.pipeline.DebatesToXMIPipeline inputFolderWithXMLFiles outputFolderWithXMIFiles

2. Prepare data for CLUTO clustering
	$cd dependencies/xxx.web.comments.clustering

- Run xxx.web.comments.clustering.ClutoMain word2VecFile sourceDataDir cacheFile tfidfModel clutoMatrixFile

and provide word2VecFile (download GoogleNews-vectors-negative300.bin.gz from https://code.google.com/p/word2vec/), source dir (outputFolderWithXMIFiles from the previous step), the other three files will be newly created

3. Run CLUTO

- Download from http://glaros.dtc.umn.edu/gkhome/cluto/cluto/download

	$vcluster -clmethod=rbr -crfun=i2 -sim=cos clutoMatrixFile numberOfClusters

4. Create centroids

	$cd dependencies/xxx.web.comments.clustering

- Run xxx.web.comments.clustering.ClusterCentroidsMain clutoMatrixFile clutoOutput outputCentroids

5. Inject centroids into the experiment code to main/src/main/resources/clusters and modify xxx.argumentation.sequence.feature.clustering.ArgumentSpaceFeatureExtractor, then run the experiments as described above

[Optional pre-step 0] Using another unlabeled dataset (i.e., newer version of createdebate.com)

- crawl createdebate.com using e.g. apache Nutch and extract the HTML content (using e.g. github.com/xxxx)
- convert HTML to internal XML documents

	$cd xxx.web.comments/xxx.web.comments.debates/target
	$java -cp lib/*:xxx.web.comments.extractors-0.1-SNAPSHOT.jar xxx.web.comments.debates.CorpusPreparator htmlFolder outputFolder

