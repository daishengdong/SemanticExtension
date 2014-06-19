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
	 * ͳ�ƣ�ʵ�����¹���
	(1) ͳ��term������collection�е��ĵ�Ƶ��(document frequency, DF)��
	(2) ͳ��term������collection�г��ֵĴʴ�(term frequency in whole collection)��
	(3) ͳ��term��ĳ���ĵ��г��ֵ�Ƶ��(term frequency, TF)��
	(4) �г�term��ĳ�ĵ��г��ֵ�λ��(position)��
	(5) ����collection���ĵ��ĸ�����
	 * */
	public static void printIndex(IndexReader reader) throws Exception{

		//��ʾdocument��
		System.out.println(new Date()+"\n");
		System.out.println(reader+"\t���������� "+reader.numDocs()+"ƪ�ĵ� ");

		for(int i=0;i<reader.numDocs();i++){
			System.out.println("�ĵ�"+i+"��"+reader.document(i)+"\n");
		}

		//ö��term�������Ϣ
		TermEnum termEnum=reader.terms();
		while(termEnum.next()){
			System.out.println("\n"+termEnum.term().field()+"���г��ֵĴ��"+termEnum.term().text());
			System.out.println(" ���ָôʵ��ĵ���="+termEnum.docFreq());

			TermPositions termPositions=reader.termPositions(termEnum.term());
			int i=0;
			int j=0;
			while(termPositions.next()){
				System.out.println("\n"+(i++)+"->"+" ���±��:"+termPositions.doc()+", ���ִ���:"+termPositions.freq()+" ����λ�ã�");
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
        // ����Ŀ¼��һ��indexReader  
        indexReader = IndexReader.open(directory);  
		printIndex(indexReader);
	}
}
