How to modify Jaxb Pojos for configuration files:
Note: valid on Windows environment 

1. Modify parallelj.xsd
2. Go to src/scripts with a command prompt (DOS windows)
3. run gen.bat
   The Jaxb Pojos should be available in src/main/java/org/parallelj/internal/conf
4. Modify the generated file org.parallelj.internal.conf.ParalleljConfiguration as this:
   - add "import javax.xml.bind.annotation.XmlRootElement;"
   - add this annotation on the class: @XmlRootElement(name="parallelj")

Note: the gen.bat creates a parallelj.episode file in the target folder.
This file may be used to extend the ParallelJ configuration file.