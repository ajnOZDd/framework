package models ;
public class Mapping {
  private String classname ; 
  private String methodname ;

public Mapping(String classname, String methodname) {
    this.setClassName(classname);
    this.setMethodName(methodname);
}

public void setClassName (String classname){
     this.classname = classname ;
  }

  public String getClassName(){
     return  classname ;
  }

  public void setMethodName ( String methodname){
      this.methodname = methodname ;
  }

  public String getMethodName (){
     return methodname ;
  }

  
}
