namespace Project1.TypeDependencies;

public interface IFirstParentInterface { }
public interface ISecondParentInterface { }

public interface IFirstChildInterface : IFirstParentInterface, ISecondParentInterface { }
public interface ISecondChildInterface : IFirstParentInterface, ISecondParentInterface { }
