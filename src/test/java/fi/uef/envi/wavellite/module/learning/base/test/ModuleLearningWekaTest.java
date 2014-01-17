/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning.base.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import weka.classifiers.functions.MultilayerPerceptron;

import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyBase;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueDouble;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueInteger;
import fi.uef.envi.wavellite.entity.derivation.base.DatasetObservationBase;
import fi.uef.envi.wavellite.entity.situation.ElementaryInfon;
import fi.uef.envi.wavellite.entity.situation.Polarity;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.entity.situation.base.AttributeRelevantIndividual;
import fi.uef.envi.wavellite.entity.situation.base.AttributeValueString;
import fi.uef.envi.wavellite.entity.situation.base.ElementaryInfonBase;
import fi.uef.envi.wavellite.entity.situation.base.RelationBase;
import fi.uef.envi.wavellite.entity.situation.base.RelevantIndividualBase;
import fi.uef.envi.wavellite.entity.situation.base.SituationBase;
import fi.uef.envi.wavellite.module.ModuleResult;
import fi.uef.envi.wavellite.module.learning.base.AbstractModuleLearning;
import fi.uef.envi.wavellite.operator.extraction.base.weka.SituationExtractorListenerWeka;
import fi.uef.envi.wavellite.operator.extraction.base.weka.SituationExtractorWeka;

/**
 * <p>
 * Title: ModuleLearningWekaTest
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Wavellite Module
 * </p>
 * <p>
 * Copyright: Copyright (C) 2013
 * </p>
 * 
 * @author Markus Stocker
 */

public class ModuleLearningWekaTest {

	@Test
	public void test1() {
		ModuleResult<DatasetObservation, Situation> m = new TestModuleLearning();
		Queue<Situation> q = m.result();
		
		DatasetObservation o = new DatasetObservationBase();

		o.addComponent(new ComponentPropertyBase("p1"),
				new ComponentPropertyValueInteger(6));
		o.addComponent(new ComponentPropertyBase("p2"),
				new ComponentPropertyValueDouble(12.0));
		o.addComponent(new ComponentPropertyBase("p3"),
				new ComponentPropertyValueInteger(18));

		m.consider(o);
		
		Situation a = q.poll();

		Situation e = new SituationBase("s1");
		ElementaryInfon i = new ElementaryInfonBase("i1");
		i.setRelation(new RelationBase("r1"));
		i.addRelevantObject(new RelevantIndividualBase("o1", new AttributeRelevantIndividual(
				"a1", new AttributeValueString("v1", "A"))));
		i.setPolarity(Polarity.True);
		e.addSupportedInfon(i);

		assertEquals(e, a);
	}
	
	@Test
	public void test2() {
		ModuleResult<DatasetObservation, Situation> m = new TestModuleLearning();
		Queue<Situation> q = m.result();

		DatasetObservation o = new DatasetObservationBase();

		o.addComponent(new ComponentPropertyBase("p1"),
				new ComponentPropertyValueInteger(6));
		o.addComponent(new ComponentPropertyBase("p2"),
				new ComponentPropertyValueDouble(12.0));
		o.addComponent(new ComponentPropertyBase("p3"),
				new ComponentPropertyValueInteger(18));

		m.consider(o);

		Situation a = q.poll();

		Situation e = new SituationBase("s1");
		ElementaryInfon i = new ElementaryInfonBase("i1");
		i.setRelation(new RelationBase("r1"));
		i.addRelevantObject(new RelevantIndividualBase("o1", new AttributeRelevantIndividual(
				"a1", new AttributeValueString("v1", "B"))));
		i.setPolarity(Polarity.True);
		e.addSupportedInfon(i);

		assertNotEquals(e, a);
	}

	private class TestModuleLearning extends AbstractModuleLearning {

		private SituationExtractorWeka o;

		public TestModuleLearning() {
			try {
				o = new SituationExtractorWeka(new MultilayerPerceptron(),
						"src/test/resources/test.modulelearningweka.1.arff");
			} catch (Exception e) {
				e.printStackTrace();
			}

			o.setListener(new TestKnowledgeAcquirerListener());
		}

		@Override
		public void considerAll(Collection<DatasetObservation> observations) {
			queue.addAll(o.acquire(observations));
		}

		private class TestKnowledgeAcquirerListener implements
				SituationExtractorListenerWeka {

			private Set<Situation> situations;
			
			public TestKnowledgeAcquirerListener() {
				situations = new HashSet<Situation>();
			}
			
			@Override
			public void onClassification(String label) {
				Situation s = new SituationBase("s1");
				ElementaryInfon i = new ElementaryInfonBase("i1");
				i.setRelation(new RelationBase("r1"));
				i.addRelevantObject(new RelevantIndividualBase("o1",
						new AttributeRelevantIndividual("a1", new AttributeValueString("v1",
								label))));
				i.setPolarity(Polarity.True);
				s.addSupportedInfon(i);

				situations.add(s);
			}

			@Override
			public Set<Situation> getSituations() {
				return situations;
			}
		}

	}

}
