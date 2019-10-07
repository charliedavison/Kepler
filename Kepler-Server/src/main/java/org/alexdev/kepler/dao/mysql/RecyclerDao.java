package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;
import org.alexdev.kepler.game.recycler.RecyclerReward;
import org.alexdev.kepler.game.recycler.RecyclerSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecyclerDao {
    public static List<RecyclerReward> getRewards() {
        List<RecyclerReward> recyclerRewardList = new ArrayList<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM recycler_rewards ORDER BY id ASC", sqlConnection);
            resultSet =  preparedStatement.executeQuery();

            while (resultSet.next()) {
                recyclerRewardList.add(new RecyclerReward(resultSet.getInt("id"), resultSet.getString("sale_code"), resultSet.getInt("item_cost")));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return recyclerRewardList;
    }

    public static RecyclerSession getSession(int userId) {
        RecyclerSession recyclerSession = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM recycler_sessions WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            resultSet =  preparedStatement.executeQuery();

            if (resultSet.next()) {
                recyclerSession = new RecyclerSession(resultSet.getInt("reward_id"), resultSet.getTime("session_started").getTime() / 1000L, resultSet.getBoolean("is_claimed"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
            Storage.closeSilently(resultSet);
        }

        return recyclerSession;
    }

    public static void createSession(int userId, int rewardId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("INSERT INTO recycler_sessions (user_id, reward_id) VALUES (?, ?)", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, rewardId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    public static void claimItem(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("UPDATE recycler_sessions SET is_claimed = 1 WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }


    public static void deleteSession(int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("DELETE FROM recycler_sessions WHERE user_id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
}
