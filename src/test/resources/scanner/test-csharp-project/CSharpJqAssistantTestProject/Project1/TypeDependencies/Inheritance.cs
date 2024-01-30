namespace Project_1.TypeDependencies;

public abstract class ParentClass { }
public class FirstChildClass : ParentClass { }
public class SecondChildClass : ParentClass { }

public record ParentRecord {}
public record FirstChildRecord : ParentRecord {}
public record SecondChildRecord : ParentRecord {}
