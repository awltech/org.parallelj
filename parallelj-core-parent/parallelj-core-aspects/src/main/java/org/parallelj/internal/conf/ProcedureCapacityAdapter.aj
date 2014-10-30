/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.parallelj.internal.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.parallelj.internal.conf.pojos.CProcedure;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.procedure.SubProcessProcedure;
import org.parallelj.mirror.Procedure;

public privileged aspect ProcedureCapacityAdapter {
	
	private List<CProcedure> getProceduresConfiguration() {
		ConfigurationService service = ConfigurationService
				.getConfigurationService();
		if (service != null) {
			Map<Class<?>, ConfigurationManager> managers = service
					.getConfigurationManager();
			if (managers != null) {
				ParalleljConfigurationManager pManager = (ParalleljConfigurationManager) managers
						.get(ParalleljConfigurationManager.class);
				if (pManager != null) {
					ParalleljConfiguration configuration = (ParalleljConfiguration) pManager
							.getConfiguration();

					// Get the Program instance Procedures from configuration file
					if (configuration.getProcedures() != null
							&& configuration.getProcedures().getProcedure() != null) {
						return configuration.getProcedures()
								.getProcedure();
					}
				}
			}
		}
		return new ArrayList<>();
	}
	
	void around(KProcessor kProcessor, KProcess kProcess): execution(public void execute(..))
	&& this(kProcessor)
	&& args(kProcess) {
		
		List<CProcedure> porceduresConfiguration = getProceduresConfiguration();
		KProgram program = (KProgram)kProcess.getProgram();
		setProcedureCapacityFromConfiguration(program, porceduresConfiguration);
		proceed(kProcessor, kProcess);
	}
	
	private void setProcedureCapacityFromConfiguration(KProgram program, List<CProcedure> proceduresConfiguration) {
		for (CProcedure cProcedure : proceduresConfiguration) {
			if (program.getName().equals(cProcedure.getName())) {
				program.setCapacity(cProcedure.getCapacity().shortValue());
			}
		}
		for (Procedure procedure : program.getProcedures()) {
			KProcedure kprocedure = (KProcedure)procedure;
			Class<?> kprocedureClass = kprocedure.getClass(); 
			for (CProcedure cProcedure : proceduresConfiguration) {
				if (kprocedure.getName()!=null && kprocedure.getName().equals(cProcedure.getName())) {
					kprocedure.setCapacity(cProcedure.getCapacity().shortValue());
				}
			}
			if (kprocedureClass.equals(SubProcessProcedure.class)) {
				KProgram subProgram = ((SubProcessProcedure)kprocedure).getSubProgram();
				setProcedureCapacityFromConfiguration(subProgram, proceduresConfiguration);
			}
		}
	}
}
