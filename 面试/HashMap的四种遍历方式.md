# HashMap的五种遍历方式

```
HashMap<Integer, String> map = new HashMap<>();
```



## 方式一

遍历Entry

```
for(Map.Entry<Integer, String> entry : map.entrySet()) {
	entry.getKey();
	entry.getValue();
}
```



## 方式二

Iterator

```
Iterator<Map.Entry<Integer, String>> iterator = map.entrySet.iterator();
while(iterator.hasNext()) {
	Map.Entry<Integer, String> entry = iterator.next();
	entry.getKey();
	entry.getValue();
}

```



## 方式三

遍历key

```
for(Integer i : map.keySet()) {
	
} 
```



## 方式四

遍历value

```
for(String s : map.values()) {
	
}
```



## 方式五

foreach()

```
map.foreach((key, value) -> ....);
```

