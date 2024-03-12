package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String,User> allUsers; //mobile->user object
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.allUsers = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception{
        if(userMobile.contains(mobile))
            throw new Exception("User already exists");
        else{
            userMobile.add(mobile);
            allUsers.put(mobile,new User(name,mobile));
            return "SUCCESS";
        }
    }

    public Group createGroup(List<User> users){
        if(users.size()<=1)
            return null;
        if(users.size()==2){
            Group group=new Group(users.get(1).getName(),users.size());
            groupUserMap.put(group,users);
            adminMap.put(group,users.get(0));
            for(User u:users)
                allUsers.put(u.getMobile(),u);
            return group;
        }
        else{
            String groupName="Group "+ (customGroupCount+1);
            Group group=new Group(groupName,users.size());
            groupUserMap.put(group,users);
            adminMap.put(group,users.get(0));
            customGroupCount++;
            for(User u:users)
                allUsers.put(u.getMobile(),u);
            return group;
        }
    }

    public int createMessage(String content){
        messageId++;
        Message message=new Message(messageId,content,new Date());
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        else{
            List<User> users=groupUserMap.get(group);
            int i=0;
            for(;i<users.size();i++)
                if(sender.equals(users.get(i)))
                    break;
            if(i==users.size())
                throw new Exception("You are not allowed to send message");
            if(!groupMessageMap.containsKey(group))
                groupMessageMap.put(group,new ArrayList<Message>());
            groupMessageMap.get(group).add(message);
            senderMap.put(message,sender);
            return groupMessageMap.get(group).size();
        }
    }

    public String changeAdmin(User approver, User user,Group group) throws Exception{
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        if(!adminMap.get(group).equals(approver))
            throw new Exception("Approver does not have rights");
        List<User> users=groupUserMap.get(group);
        int i=0;
        for(;i<users.size();i++)
            if(user.equals(users.get(i)))
                break;
        if(i==users.size())
            throw new Exception("User is not a participant");
        adminMap.put(group,user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{
        if(!allUsers.containsKey(user.getMobile()))
            throw new Exception("User not found");
        if(adminMap.containsValue(user))
            throw new Exception("Cannot remove admin");
        int ans=0;
        //removing from senderMap
        List<Message> message=new ArrayList<>();
        for(Message msg:senderMap.keySet()){
            if(senderMap.get(msg).equals(user)) {
                message.add(msg);
            }
        }
        for(int i=0;i<message.size();i++)
            senderMap.remove(message.get(i));
        //remove from allUsers and userMobile
        String mobile=null;
        for(String key:allUsers.keySet()){
            if(key.equals(user.getMobile())) {
                mobile = key;
                break;
            }
        }
        allUsers.remove(mobile);
        userMobile.remove(mobile);
        //remove from groupUserMap
        ArrayList<Group> groups=new ArrayList<>();
        for(Group grp:groupUserMap.keySet()){
            List<User> list=groupUserMap.get(grp);
            boolean flag=false;
            for(User usr:list){
                if(user.equals(usr)){
                    flag=true;
                    break;
                }
            }
            if(flag)
                list.remove(user);
            if(list.size()<2 && flag){
                //groupUserMap.remove(grp);
                groups.add(grp);
                groupMessageMap.remove(grp);
            }
//            else if(list.size()==2 && flag){
//                customGroupCount--;
//            }
            if(flag && list.size()>=2)
                ans+=list.size();
            ans+=groupMessageMap.get(grp).size();
            //removing from groupMessageMap
            if(groupMessageMap.containsKey(grp)){
                for(int i=0;i<message.size();i++)
                    if(groupMessageMap.get(grp).contains(message.get(i))){
                        groupMessageMap.get(grp).remove(message);
                   //     ans--;
                    }
            }
        }
        for(int i=0;i<groups.size();i++)
            groupUserMap.remove(groups.get(i));
        return ans;
    }

    public String findMessage(Date start, Date end, int k) throws Exception{
        List<String> ans=new ArrayList<>();
        for(Group grp:groupMessageMap.keySet()){
            List<Message> list=groupMessageMap.get(grp);
            for(Message mssg:list){
                if(mssg.getDate().after(start) && mssg.getDate().before(end))
                    ans.add(mssg.getContent());
            }
        }
        Collections.sort(ans);
        if(ans.size()>k)
            return ans.get(k-1);
        else throw new Exception("K is greater than the number of messages");
    }
}
