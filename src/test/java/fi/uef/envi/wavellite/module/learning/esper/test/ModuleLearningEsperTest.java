/*
 * Copyright (C) 2013 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.wavellite.module.learning.esper.test;

import org.joda.time.DateTime;
import org.junit.Test;

import fi.uef.envi.wavellite.entity.core.base.TemporalLocationDateTime;
import fi.uef.envi.wavellite.entity.derivation.DatasetObservation;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyBase;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueDouble;
import fi.uef.envi.wavellite.entity.derivation.base.ComponentPropertyValueTemporalLocation;
import fi.uef.envi.wavellite.entity.derivation.base.DatasetObservationBase;
import fi.uef.envi.wavellite.entity.situation.Situation;
import fi.uef.envi.wavellite.module.learning.ModuleLearningListener;
import fi.uef.envi.wavellite.module.learning.esper.ModuleLearningEsper;
import fi.uef.envi.wavellite.vocabulary.SDMX;

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
		// String e = "select * from DatasetObservationType "
		// + "where components[1].componentProperty.id = 'p1' " +
		// "and cast(components[1].componentPropertyValue.value, double) > 0.0";
		String e = "select *, avg(components[1].componentPropertyValue.value) as avg "
				+ "from DatasetObservationType.win:time_batch(5 sec) " +
				"having avg(components[1].componentPropertyValue.value) >= 0.5";

		ModuleLearningEsper m = new ModuleLearningEsper();
		m.addListener(new ThisModuleLearningListener());
		m.addExpression(e);

		DatasetObservation o1 = new DatasetObservationBase();
		o1.addComponent(new ComponentPropertyBase(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(new DateTime(2013, 11, 05,
								0, 0, 0))));
		o1.addComponent(new ComponentPropertyBase("p1"),
				new ComponentPropertyValueDouble(0.5));
		m.addDatasetObservation(o1);

		DatasetObservation o2 = new DatasetObservationBase();
		o2.addComponent(new ComponentPropertyBase(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(new DateTime(2013, 11, 05,
								0, 0, 5))));
		o2.addComponent(new ComponentPropertyBase("p1"),
				new ComponentPropertyValueDouble(-0.5));
		m.addDatasetObservation(o2);

		DatasetObservation o3 = new DatasetObservationBase();
		o3.addComponent(new ComponentPropertyBase(SDMX.Dimension.timePeriod),
				new ComponentPropertyValueTemporalLocation(
						new TemporalLocationDateTime(new DateTime(2013, 11, 05,
								0, 0, 10))));
		o3.addComponent(new ComponentPropertyBase("p1"),
				new ComponentPropertyValueDouble(0.5));
		m.addDatasetObservation(o3);
	}

	private class ThisModuleLearningListener implements ModuleLearningListener {

		@Override
		public void onSituation(Situation situation) {
			System.out.println(situation);
		}

	}

}
