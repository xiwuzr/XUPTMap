package com.service.impl;

import com.mapper.PathMapper;
import com.mapper.PlaceMapper;
import com.pojo.PageInfo;
import com.pojo.Path;
import com.pojo.Place;

import java.util.*;

public class ServiceImpl {
    private PathMapper pathMapper;
    private PlaceMapper placeMapper;
    node[] map;//邻接表
    Map<String, Integer> index;
    int shortPath;
    int lowPath;
    int k = 0;
    int minStep = 32767;
    int minSum = 32767;
    List arrayList = new ArrayList();
    List<List<String>> main = null;//遍历结果
    List<String> main2 = null;


    public void init(){

        int size = placeMapper.selCount();
        map = new node[size];
        List<Place> places = placeMapper.SelList();
        int i=0;
        for (Place place : places) {
            map[i] = new node();
            map[i].placeName = place.getName();
            index.put(place.getName(), i);
            i++;
        }
        //将数组转成邻接表
        for(int j = 0 ; j < size; j++)
        {
            String place = map[j].placeName;
            node temp = map[j];
            node prev = map[j];
            List<Path> paths = pathMapper.selPath(place);//路径数组
            for (Path path : paths) {
//                System.out.println(path.getEnd()+ path.getStart());
                //这个路径的起点或者终点是这个节点，就讲另一个节点连到链表的后面
                if(path.getStart().equals(place))
                {
                    temp = new node(path.getEnd(), path.getLength());
                    temp.setNext(null);
                    prev.setNext(temp);
                    prev = temp;
                }
                if(path.getEnd().equals(place))
                {
                    temp = new node(path.getStart(), path.getLength());
                    temp.setNext(null);
                    prev.setNext(temp);
                    prev = temp;
                }
            }
        }

    }

    public Path getmin(List<Path> list){
        Path tt=null;
        int min=10000;
        for(Path mm:list){
            if(mm.getLength()<min)
            {
                min=mm.getLength();
                tt=mm;
            }
        }
        return tt;
    }

    public int getRoot(int book[],int a){
        if(book[a]!=a){
            return getRoot(book,book[a]);
        }else{
            return a;
        }


    }


    public Set getPer(){
        index=new HashMap<>();
        Set<Path> set=new HashSet<>();
        init();
        List<Place> places = placeMapper.SelList();
        List<Path> paths = pathMapper.selAll();//路径数组
        int size=placeMapper.selCount();
        int[] book=new int[size];
        for(int i=0;i<book.length;i++){
            book[i]=i;
        }
        Comparator<? super Path> comparator =new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o1.getLength()-o2.getLength();
            }
        } ;
        paths.sort(comparator);
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&");
        for(Path pp:paths){
            System.out.println(pp);
        }

        int kk=0;
        index.get("基础教学楼");


        for(Path ps:paths){
            String s1=ps.getStart();
            String s2=ps.getEnd();
            System.out.println(index);
            System.out.println(s1+"()()()()()");
            System.out.println(s2+"()()()()()");

            System.out.println(index.get("西区大门"));
                int c = index.get(s1);
                int d = index.get(s2);
                int a = getRoot(book, c);
                int b = getRoot(book, d);
                if ( a == b ) {
                    continue;
                } else {
                    kk++;
                    set.add(ps);
                    book[ a ] = b;

                }
                if ( kk == size - 1 ) {
                    break;
                }
            System.out.println("+++++++++++++");

            for(Path ppp:set){
                System.out.println(ppp);
            }

        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^");
        for(Path ppp:set){
            System.out.println(ppp);
        }
        //source E:\sql\path.sql
        //source E:\sql\place.sql
        return set;
    }



    public int getIndexByName(String start){
        List<Place> places = placeMapper.SelList();
        int i=0;
        int k=-1;
        for(Place place:places){
            if(place.getName().equals(start)){
                k=i;
                break;
            }
            i++;
        }
        return k;
    }

    public String getNameByIndex(int n){
        List<Place> places = placeMapper.SelList();
        return (String)places.get(n).getName();
    }

    public Map dijkstra(String start){
        int k=0,t=1;
        Map result = new HashMap();
        int size = placeMapper.selCount();
        int[] dist=new int[size+1];
        for(int i=0;i<size+1;i++){
            dist[i]=Integer.MAX_VALUE;
        }
        int pa[][] =new int[size+1][size+1];
        for(int i=0;i<size+1;i++){
            for(int j=1;j<size+1;j++){
                pa[i][j]=-1;
            }
        }
        init();//得到邻接表
        int n=index.get(start);
        node nn=map[n];

        while(nn!=null){
            dist[index.get(nn.getPlaceName())]=nn.length;
            nn=nn.next;
        }

        pa[n][0]=1;
        int mindist=Integer.MAX_VALUE;
        for(int j=0;j<size-1;j++){
            if(pa[j][0]!=0&&dist[j]!=0&&dist[j]<mindist){
                {
                    k=j;
                    mindist=dist[j];
                }
            }
        }
        if(mindist==Integer.MAX_VALUE)//没有最短路径
            return null;
        pa[k][0]=1;
        nn=map[k].next;
        pa[k][1]=index.get(start);

            while(nn!=null){
                if(nn.length+dist[k]<dist[index.get(nn.placeName)]){
                    dist[index.get(nn.placeName)]=nn.length+dist[k];
                    t=1;
                    while(pa[k][t]!=-1){
                        pa[index.get(nn.placeName)][t]=pa[k][t];
                        t++;
                    }
                    pa[index.get(nn.placeName)][t]=k;
                    pa[index.get(nn.placeName)][t+1]=-1;
                }
            }

        System.out.println("*****************");
        for(int i=0;i<3;i++){
            for(int j:pa[i]){
                System.out.print(j+" ");
            }
            System.out.println("");
        }

        return result;

    }

    public Map query(String start, String end)//开始到结束的路径放到Map容器中
    {
        Map result = new HashMap();
       // System.out.println(result+"**result");
        int size = placeMapper.selCount();//得到所有的地点数
        index = new HashMap();
        init();

        //深度优先搜索
        k=0;
        main = new ArrayList<>();
        main2=new ArrayList<>();
        dfs(start, end, 0, 0);

        StringBuilder  sp = new StringBuilder("");
        //最短路径
        for (String s : main.get(shortPath)) {
            sp.append(s + "  ");
        }
        //中转最少的路径
        StringBuilder lp = new StringBuilder("");
        for (String s : main.get(lowPath) ) {
            lp.append(s + "  ");
        }
        //所有的路径
        for (List<String> strings : main) {
            StringBuilder sb = new StringBuilder("");
            for (String string : strings) {
                sb.append(string+" ");
            }
            main2.add(sb.toString());
        }
       // System.out.println(result+"***result111");

        result.put("shortPath", sp);
        result.put("lowPath", lp);
        result.put("minSum", minSum);
        result.put("main2", main2);
        //        node temp;
//        for (node node : map) {
//            temp = node;
//            while (temp != null)
//            {
//                System.out.print(temp.getPlaceName() + temp.getLength());
//                temp = temp.next;
//            }
//            System.out.println();
//        }
//        return;
//
//        System.out.println(shortPath + " " + minSum);
//        System.out.println(lowPath +" " +minStep);

//            while (temp != null)
//            {
//                System.out.print(temp.getPlaceName() + temp.getLength());
//                temp = temp.next;
//            }
//            System.out.println();
//        }
        return result;
    }

    public void dfs(String name, String end, int step, int sum)
    {

        node temp;
        Integer i = index.get(name);
        if(i == null)
            return;
        if(map[i].step == true)
            return;
        step++;
        map[i].step = true;
        if(map[i].placeName.equals(end)) {
            if (step < minStep) {
                minStep = step;
                lowPath = k;
            }
            if (sum < minSum) {
                minSum = sum;
                shortPath = k;
            }
        }
        arrayList.add(name);
        List<String> t = new ArrayList();
        for(int m = 0; m < arrayList.size(); m++)
        {
            t.add((String) arrayList.get(m));
        }

        if(map[i].placeName.equals(end)){
            main.add(k++, t);
        }

        temp = map[i].next;
        while (temp != null)
        {
            dfs(temp.placeName, end, step, sum + temp.getLength());
            temp = temp.next;
        }
        map[i].step = false;
        arrayList.remove(name);
    }

    class node{
        String placeName;//地点名称
        int length;//路径长度
        boolean step;//标记是否走过
        node next;//下一个节点

        public node() {
        }

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public boolean isStep() {
            return step;
        }

        public void setStep(boolean step) {
            this.step = step;
        }

        public node getNext() {
            return next;
        }

        public void setNext(node next) {
            this.next = next;
        }

        public node(String placeName, int length){
                this.placeName = placeName;
                this.length = length;
        }

        @Override
        public String toString() {
            return "node{" +
                    "placeName='" + placeName + '\'' +
                    ", length=" + length +
                    ", step=" + step +
                    '}';
        }
    }
    public int insPlace(String name, String msg)
    {
        return placeMapper.insPlace(name, msg);
    }

    public int insPath(String start, String end, int length)
    {
        return pathMapper.insPath(start, end, length);
    }
    public int delPath(String start, String end)
    {

        return pathMapper.delete(start, end);
    }
    public int delPlace(String name)
    {
        pathMapper.delete2(name);
        return placeMapper.delete(name);
    }
    public PageInfo showPlace(int pageNum)
    {
        int pageSize = 10;
        PageInfo pageInfo = new PageInfo(pageNum, pageSize);
        int count = placeMapper.selCount();
        pageInfo.setPageSum(count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        pageInfo.setList(placeMapper.selAll((pageNum-1) * pageSize, pageSize));
        return pageInfo;
    }

    public List selList()
    {
        return placeMapper.SelList();
    }
    public List showPath(String name)
    {
        List<Path> paths = pathMapper.selByStart(name);
        return paths;
    }

    public Place showPlaceByName(String name)
    {
        return placeMapper.selByName(name);
    }

    public List showPathByStart(String start)
    {
        return pathMapper.selByStart(start);
    }
    public PathMapper getPathMapper() {
        return pathMapper;
    }

    public void setPathMapper(PathMapper pathMapper) {
        this.pathMapper = pathMapper;
    }

    public PlaceMapper getPlaceMapper() {
        return placeMapper;
    }

    public void setPlaceMapper(PlaceMapper placeMapper) {
        this.placeMapper = placeMapper;
    }


}
