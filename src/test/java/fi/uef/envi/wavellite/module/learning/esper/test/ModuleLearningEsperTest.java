/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning.esper.test;

import org.junit.Test;

import fi.uef.envi.wavellite.entity.derivation.ComponentProperty;
import fi.uef.envi.wavellite.entity.derivation.ComponentPropertyValue;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyBase;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueDouble;
import fi.uef.envi.wavellite.entity.derivation.base.DatasetObservationBase;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.module.learning.ModuleLearningListener;
import fi.uef.envi.wavellite.module.learning.esper.ModuleLearningEsper;

/**
 * <p>
 * Title: ModuleLearningEsperTest
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

public class ModuleLearningEsperTest {

	@Test
	public void test1() {
		String e = "select * from DatasetObservation "
				+ "where components?.componentProperty.id = 'p1' "
				+ "and cast(components?.componentPropertyValue.value, double) > 0.0";

		ModuleLearningEsper m = new ModuleLearningEsper();
		m.addListener(new ThisModuleLearningListener());
		m.addExpression(e);

		DatasetObservation o1 = new DatasetObservationBase();
		o1.addComponentProperty(new ComponentPropertyBase("p1"),
				new ComponentPropertyValueDouble(0.5));

		m.addDatasetObservation(o1);
		
		DatasetObservation o2 = new DatasetObservationBase();
		o2.addComponentProperty(new ComponentPropertyBase("p1"),
				new ComponentPropertyValueDouble(1.0));

		m.addDatasetObservation(o2);
	}

	private class ThisModuleLearningListener implements ModuleLearningListener {

		@Override
		public void onSituation(Situation situation) {
			System.out.println(situation);
		}

	}

}
