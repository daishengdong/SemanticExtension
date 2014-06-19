package com.fatty.ontology.reasoning;

import java.io.File;
import java.util.Date;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class Statistic {
	/*
	 * 统计，实现以下功能
	(1) 统计term在整个collection中的文档频度(document frequency, DF)；
	(2) 统计term在整个collection中出现的词次(term frequency in whole collection)；
	(3) 统计term在某个文档中出现的频度(term frequency, TF)；
	(4) 列出term在某文档中出现的位置(position)；
	(5) 整个collection中文档的个数；
	 * */
	public static void printIndex(IndexReader reader) throws Exception{

		//显示document数
		System.out.println(new Date()+"\n");
		System.out.println(reader+"\t该索引共含 "+reader.numDocs()+"篇文档 ");

		for(int i=0;i<reader.numDocs();i++){
			System.out.println("文档"+i+"："+reader.document(i)+"\n");
		}

		//枚举term，获得信息
		TermEnum termEnum=reader.terms();
		while(termEnum.next()){
			System.out.println("\n"+termEnum.term().field()+"域中出现的词语："+termEnum.term().text());
			System.out.println(" 出现该词的文档数="+termEnum.docFreq());

			TermPositions termPositions=reader.termPositions(termEnum.term());
			int i=0;
			int j=0;
			while(termPositions.next()){
				System.out.println("\n"+(i++)+"->"+" 文章编号:"+termPositions.doc()+", 出现次数:"+termPositions.freq()+" 出现位置：");
				// for(j=0;j System.out.println("\n");
			}

			/*TermDocs termDocs=reader.termDocs(termEnum.term());
while(termDocs.next()){
System.out.println((i++)+"->DocNo:"+termDocs.doc()+",Freq:"+termDocs.freq());
}*/
		}

	}

	public static void main(String args[]) throws Exception{
	    File indexFile = new File("indexs/");  
        Directory directory = null;  
        IndexReader indexReader = null;  
        directory = new SimpleFSDirectory(indexFile);  
        // 根据目录打开一个indexReader  
        indexReader = IndexReader.open(directory);  
		printIndex(indexReader);
	}
}
