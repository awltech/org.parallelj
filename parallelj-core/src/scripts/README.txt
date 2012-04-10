How to modify Jaxb Pojos for configuration files:
Note: valid on Windows environment 

1. Modify parallelj.xsd
2. Remove all classes in package org.parallelj.internal.conf.pojos
3. Go to src/scripts with a command prompt (DOS windows)
4. run gen.bat
   The Jaxb Pojos should be available in src/main/java/org/parallelj/internal/conf/pojos
5. Modify the generated file org.parallelj.internal.conf.pojos.ParalleljConfiguration as this:
   - @XmlRootElement(name="ParallejConfiguration") => @XmlRootElement(name="parallelj") 

