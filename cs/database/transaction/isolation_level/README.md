# 1. 격리 수준
> 격리 수준은 여러 트랜잭션이 병행 실행될 때 서로 다른 트랜젝션에서 변경, 
> 조회하는 데이터들을 어느 정도 수준에서 볼 수 있는지 설정한다.

> Mysql은 데이터를 변경할 때 변경 전 데이터를 UNDO 영역에 저장하고, 변경된 데이터는 바로바로 레코드에 저장한다.   
> 따라서 격리된 데이터를 조회할 때 UNDO 영역의 레코드를 조회한다.

# 2. 격리 수준에 따른 문제점
> SELECT를 수행할 때는 공유 잠금(Shared Lock)이 걸린다.   
> INSERT/UPDATE/DELETE를 수행할 경우에는 배타적 잠금(Exclusive Lock)이 걸린다.

## 2.1 DIRTY READ
> 트랜잭션의 작업이 아직 다 끝나지 않아도 다른 트랜잭션의 내용을 볼 수 있는 것,   
> 변경 된 데이터는 롤백될수도, 커밋될수도 있는 상황에서 데이터를 참고하므로 데이터 정합성에 큰 문제 유발

> DIRTY READ를 하고 싶다면 격리 수준을 READ UNCOMMITED로 설정해야 한다.

## 2.2 NON REPEATABLE READ
> 하나의 트랜잭션에서는 같은 SELECT문으로 조회 할 떄 항상 같은 결과를 가져와야 한다. 이를 REPETABLE READ라고 한다.   
> 하지만 같은 쿼리를 두 번 수행했는데 그 사이에 다른 트랙잭션이 값을 수정 또는 삭제하는 바람에 두 쿼리가    
> 다르게 나타나는 현상을 NON REPEATABLE READ라고 한다.

> Repeatable Read는 같은 쿼리를 두 번 이상 수행할 때, 첫 번째 쿼리의 레코드가 사라지거나 값이 바뀌는 현상을 방지한다.   
> 하지만 insert는 막지 못하므로 새로운 레코드는 나타날 수 있다.

## 2.3 PHANTOM READ
> REPEATABLE READ 격리 수준에서는 공유 잠금인 상태의 데이터에 대해서도 변경 불가가 보장된다.
> 하지만 그 데이터를 변경시키지 못할 뿐, 다른 데이터의 추가/삭제는 가능하다.
> 잠금이 걸린 튜플 이외에 튜플이 생기거나 삭제되는 것을 읽는 것을 팬텀 읽기라고 한다.

> 이를 방지하려면 격리수준을 SERIALIZABLE로 설정하면 된다.   
> SERIALIZABLE는 트랜잭션이 수행중일 때 INSERT/DELETE도 제한한다.   
> 따라서 레코드가 사라지거나 값이 바뀌지 않으며, 새오운 레코드가 나타나지도 않는다.

격리수준 | DIRTY LEAD | NON REPEATABLE READ | PHANTOM READ
---|----|----|----
READ UNCOMMITTED | x|x|x
READ COMMITTED | x|o|o
REPEATABLE READ | x|x|o
SERIALIZABLE | x|x|x




