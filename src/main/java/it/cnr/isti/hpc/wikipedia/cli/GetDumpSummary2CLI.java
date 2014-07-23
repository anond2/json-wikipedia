/**
 *  Copyright 2011 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cnr.isti.hpc.wikipedia.cli;

import java.util.List;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.ArticleSummarizer;
import it.cnr.isti.hpc.wikipedia.article.Link;

/**
 * Takes the JSON dump and produce a summary file containing, a file where each
 * line contains: <br/>
 * <br/>
 * <code>
 * id <tab> tags <tab> url <tab> wikititle <tab> content
 * </code> <br/>
 * <br/>
 * 
 * The last field contains the redirection is type is redirect, otherwise the
 * content
 * 
 * 
 */
public class GetDumpSummary2CLI extends AbstractCommandLineInterface {

	private static String[] params = new String[] { INPUT, OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ GetDumpSummary2CLI.class
			+ " -input wikipedia-json-dump -output wikiout.csv";

	private final static String TAB = "\t";

	public GetDumpSummary2CLI(String[] args) {
		super(args, params, USAGE);
	}

	public static void main(String[] args) {

		GetDumpSummary2CLI cli = new GetDumpSummary2CLI(args);
		ProgressLogger pl = new ProgressLogger("dumped {} titles", 10000);
		cli.openOutput();
		RecordReader<Article> reader = new RecordReader<Article>(
				cli.getInput(), new JsonRecordParser<Article>(Article.class));

		for (Article a : reader) {
			pl.up();
			cli.writeInOutput(String.valueOf(a.getWikiId()));
			cli.writeInOutput(TAB);
			StringBuilder sb = new StringBuilder();
			for (Link l: a.getCategories()) {
				String tag = l.getId().replace("Category:", "").replace("category:", "");
				sb.append(tag).append(",");
			}
			int length = sb.length() - 1;
			if (length >= 0)
				sb.setLength(length);
			cli.writeInOutput(sb.toString());
			cli.writeInOutput(TAB);
			cli.writeInOutput("http://ja.wikipedia.org/wiki/" + a.getTitleInWikistyle());
			cli.writeInOutput(TAB);
			cli.writeInOutput(a.getWikiTitle());
			cli.writeInOutput(TAB);
			if (a.isRedirect()) {
				cli.writeInOutput("-> " + a.getRedirect());
			} else {
				StringBuilder sb2 = new StringBuilder();
				for (List<String> l: a.getLists()) {
					for (String s : l)  {
						sb2.append(s).append(" ");
					}
				}
				int length2 = sb2.length() - 1;
				if (length2 >= 0)
					sb2.setLength(length2);
				cli.writeInOutput(sb2.toString() + " " + a.getCleanText());
			}
			cli.writeInOutput("\n");

		}
		cli.closeOutput();
	}
}
