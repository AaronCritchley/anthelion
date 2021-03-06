/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.indexer.staticfield;

import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;

/** A simple plugin called at indexing that adds fields with static data. 
 *  You can specify a list of fieldname:fieldcontent per nutch job.
 *  It can be useful when collections can't be created by urlpatterns, 
 *  like in subcollection, but on a job-basis. */

public class StaticFieldIndexer implements IndexingFilter {
	private Configuration conf;
	private HashMap<String, String[]> fields;
	private boolean addStaticFields = false;

	public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks)
	    throws IndexingException {
		
		if(this.addStaticFields == true){
			for(Entry<String,String[]> entry: this.fields.entrySet()){
				doc.add(entry.getKey(), entry.getValue());
			}
		}	
		return doc;
	}

	private HashMap<String, String[]> parseFields(String fieldsString) {
		HashMap<String, String[]> fields = new HashMap<String, String[]>();
		
		/*
		  The format is very easy, it's a comma-separated list of fields in the form <name>:<value>
		*/
		for(String field: fieldsString.split(",")){
			String[] entry = field.split(":");
			if(entry.length == 2)
				fields.put(entry[0].trim(), entry[1].trim().split(" "));
		}

		return fields;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
		String fieldsString = conf.get("index.static", null);
		if(fieldsString != null){
			this.addStaticFields = true;
			this.fields = parseFields(fieldsString);
		}
	}

	public Configuration getConf() {
		return this.conf;
	}
}
