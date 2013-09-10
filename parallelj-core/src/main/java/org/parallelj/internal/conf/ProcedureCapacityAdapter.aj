package org.parallelj.internal.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.conf.pojos.CProcedure;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.kernel.KProcedure;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.procedure.CallableProcedure;
import org.parallelj.internal.kernel.procedure.RunnableProcedure;
import org.parallelj.internal.kernel.procedure.SubProcessProcedure;
import org.parallelj.internal.reflect.ProcessHelperImpl;
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
	
	@SuppressWarnings("rawtypes")
	ProcessHelperImpl around(ProcessHelperImpl processHelper, ExecutorService service): execution(public ProcessHelper execute(..))
		&& this(processHelper)
		&& args(service) {
		
		List<CProcedure> porceduresConfiguration = getProceduresConfiguration();
		KProgram program = (KProgram)processHelper.getProcess().getProgram();
		setProcedureCapacityFromConfiguration(program, porceduresConfiguration);
		ProcessHelperImpl processHelperImpl = proceed(processHelper, service);
		return processHelperImpl;
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
			if (kprocedureClass.equals(RunnableProcedure.class) || kprocedureClass.equals(CallableProcedure.class)) {
				for (CProcedure cProcedure : proceduresConfiguration) {
					if (kprocedure.getName().equals(cProcedure.getName())) {
						kprocedure.setCapacity(cProcedure.getCapacity().shortValue());
					}
				}
			} else if (kprocedureClass.equals(SubProcessProcedure.class)) {
				KProgram subProgram = ((SubProcessProcedure)kprocedure).getSubProgram();
				setProcedureCapacityFromConfiguration(subProgram, proceduresConfiguration);
			}
		}
	}
}
