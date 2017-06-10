package hm

class Training extends Item {
    
    Department department
    
    File getDir(){
        new File(Application.trainingDir,"$id").with{mkdirs();it}
    }
}
