# Exploiting Debate Portals for Argumentation Mining in User-Generated Web Discourse

WORK IN PROGRESS; to be finished before Sept 17, 2015

Source code, data, and supplementary materials to the EMNLP2015 article

```
@InProceedings{habernal-gurevych:2015:EMNLP2015,
  author    = {Habernal, Ivan and  Gurevych, Iryna},
  title     = {Exploiting Debate Portals for Argumentation Mining in User-Generated Web Discourse},
  booktitle = {Proceedings of the 2015 Conference on Empirical Methods in Natural Language Processing (EMNLP)},
  month     = {September},
  year      = {2015},
  address   = {Lisbon, Portugal},
  publisher = {Association for Computational Linguistics},
  pages     = {to appear},
  url       = {to appear}
}
```

EMNLP 2015
Readme v0.2

## Requirements

* Java 1.7 and higher, Maven
* tested on 64-bit Linux versions
* we recommend 32 GB RAM for running the experiments

## Installation

* Install all Maven dependencies from `code/dependencies/` to your local Maven repository

```
$cd code/dependencies
$chmod +x installDependencies.sh
$./installDependencies.sh
```

* You don't have to setup any other 3-rd party Maven repository location, all dependencies are located either in this folder or on Maven central.

* Compile the main experiment package in `code/experiments`

```
$cd code/experiments
$mvn package
```

## Running the experiments

```
$cd code/experiments/target
$LC_ALL=en_US.UTF-8 java -XX:+UseSerialGC -Xmx32g \
  -cp lib/*:de.tudarmstadt.ukp.dkpro.argumentation.emnlp2015-0.0.3-SNAPSHOT.jar \
  de.tudarmstadt.ukp.dkpro.argumentation.sequence.evaluation.ArgumentSequenceLabelingEvaluation \
  --featureSet fs0 \
  --corpusPath ../../../data/argumentation-gold-annotated-sentiment-discourse-rst-full-bio-embeddings-emnlp2015-final-fixed \
  --outputPath /tmp \
  --scenario cd \
  --clusters a100,s1000
```

### Parameter description:

* `--cl, --clusters`
  * Which clusters? Comma-delimited, e.g., s100,a500 (only for feature set 4)
* `--corpusPath, --c`
  * Corpus path with gold-standard annotated XMI files
* `--featureSet, --fs`
  * Feature set name (e.g., fs0, fs0fs1fs2, fs3fs4, ...)
* `--outputPath, --o`
  * Main output path (folder)
* `--paramE, --e`
  * Parameter e for SVMHMM (optional)
  * Default: 0
* `--paramT, --t`
  * Parameter T for SVMHMM (optional)
  * Default: 1
* `--scenario, --s`
  * Evaluation scenario (`cv` = cross-validation, `cd` = cross domain, `id` = in domain)

## Creating own clusters (Optional)

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

