import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleUtils extends ListenerAdapter {

    //Checks for Added Roles
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
        //Test Update Channel, can be removed or ID can be changed
        TextChannel t = e.getJDA().getTextChannelById("257900835634282496");


        try {

                //Check to see if the bot is in an accepted discord
                PreparedStatement check = DiscordMain.connection().prepareStatement("SELECT RoleID,OrgC FROM MasterList WHERE DiscordID=?");
                check.setString(1, e.getGuild().getId());
                ResultSet rs = check.executeQuery();


                //If Discord is Valid
                if(rs.next()) {
                    t.sendMessage("Valid Guild Found > Attempting Role Update\n").queue();


                    //If Role is Valid
                    if (e.getRoles().get(0).getId().equals(rs.getString(1))) {
                        t.sendMessage("Role located in database. Attempting to push role to user\n").queue();
                        e.getJDA().getGuildById("ID OF HUB DISCORD").addRoleToMember(e.getMember(), e.getJDA().getRoleById(rs.getString(2))).queue();
                        t.sendMessage("Role successfully added to user!\n").queue();
                    }
                }
                else {
                    return;
                }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            DiscordMain.connection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    //Checks for Removed Roles
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
        TextChannel t = e.getJDA().getTextChannelById("257900835634282496");
        try {
            PreparedStatement check = DiscordMain.connection().prepareStatement("SELECT RoleID,OrgC FROM MasterList WHERE DiscordID=?");
            System.out.println(e.getRoles().get(0).getId());
            System.out.println(e.getRoles().get(0).getName());

            check.setString(1, e.getGuild().getId());
            ResultSet rs = check.executeQuery();


            //If Discord is valid
            if(rs.next()) {
                t.sendMessage("Role Removal Detected in Valid Discord, checking for valid roles\n").queue();


                //If Role is Valid
                if (e.getRoles().get(0).getId().equals(rs.getString(1))) {
                    t.sendMessage("Role located in database. Attempting to remove role from user\n").queue();
                    e.getJDA().getGuildById("ID OF HUB DISCORD").removeRoleFromMember(e.getMember(), e.getJDA().getRoleById(rs.getString(2))).queue();
                    t.sendMessage("Role successfully removed from user!\n").queue();

                }
            }
            else {
                return;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            DiscordMain.connection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        TextChannel t = e.getJDA().getTextChannelById("257900835634282496");


        //Check for either SoupestGlobe or Ozzy issuing commands


        if (!(e.getMember().getId().equals("272182687454855168") || e.getMember().getId().equals("183951503726346240"))) return;
        String[] mes = e.getMessage().getContentRaw().split(" ");
        if (!mes[0].equalsIgnoreCase("$!addrole")) return;

        PreparedStatement r = null;
        PreparedStatement c = null;
        try {

            //Setup Value Check
            t.sendMessage("Authorized Command Recieved, Starting Database Update").queue();
            c = DiscordMain.connection().prepareStatement("SELECT DiscordID FROM MasterList WHERE DiscordID=?");
            c.setString(1, mes[1]);
            ResultSet cr = c.executeQuery();
            t.sendMessage("Checking for existing values...").queue();


            //Checks to see if Discord ID is already assigned to a role
            if (cr.next()) {

                //Removes row from database
                t.sendMessage("Existing values detected. Removing them now.").queue();
                PreparedStatement k = DiscordMain.connection().prepareStatement("DELETE FROM MasterList WHERE DiscordID=?");
                k.setString(1, mes[1]);
                k.executeUpdate();
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {

            //Attempts to insert data (DiscordID, RoleID, OrgCRoleID) into database
            r = DiscordMain.connection().prepareStatement("INSERT INTO MasterList (DiscordID,RoleID,OrgC) VALUES (?,?,?)");
            r.setString(1, mes[1]);
            r.setString(2, mes[2]);
            r.setString(3, mes[3]);
            r.executeUpdate();
            t.sendMessage("Pushing Values to the Database").queue();



        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
        try {
            DiscordMain.connection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    }

