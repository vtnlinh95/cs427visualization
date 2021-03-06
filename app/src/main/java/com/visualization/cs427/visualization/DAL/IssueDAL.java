package com.visualization.cs427.visualization.DAL;

import android.content.Context;

import com.visualization.cs427.visualization.DatabaseHelper.IssueDatabaseHelper;
import com.visualization.cs427.visualization.DatabaseHelper.TimeLogDatabaseHelper;
import com.visualization.cs427.visualization.Entity.ContributorEntity;
import com.visualization.cs427.visualization.Entity.IssueEntity;
import com.visualization.cs427.visualization.Entity.ProjectEntity;
import com.visualization.cs427.visualization.Exception.DatabaseException;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by linhtnvo on 9/8/2016.
 */
public class IssueDAL {
    private static IssueDAL _instance;

    public static IssueDAL getInstance() {
        if (_instance == null) {
            _instance = new IssueDAL();
        }
        return _instance;
    }

    public List<IssueEntity> getIssuebyProject(Context context, String projectID) throws DatabaseException {
        IssueDatabaseHelper helper = null;
        try {
            helper = new IssueDatabaseHelper(context);
            return helper.getIssueByProjectID(projectID);
        } finally {
            if (helper != null) {
                helper.closeConnection();
            }
        }
    }

    public List<IssueEntity> updateIssueLocation(Context context, IssueEntity issueEntity, String projectID) throws DatabaseException {
        IssueDatabaseHelper helper = null;
        try {
            helper = new IssueDatabaseHelper(context);
            return helper.updateIssueLocation(issueEntity, projectID);
        } finally {
            if (helper != null) {
                helper.closeConnection();
            }
        }
    }

    public void updateStatus(Context context, IssueEntity issueEntity, ContributorEntity contributorEntity, String spentTime) throws DatabaseException {
        IssueDatabaseHelper dbh = null;
        try {
            dbh = new IssueDatabaseHelper(context);
            dbh.updateIssueStatus(issueEntity, contributorEntity, spentTime);
        } finally {
            if (dbh != null) {
                dbh.closeConnection();
            }
        }
    }

    public String getTimeSpent(Context context, IssueEntity issueEntity, int status) throws DatabaseException {
        TimeLogDatabaseHelper dbh = null;
        try {
            dbh = new TimeLogDatabaseHelper(context);
            return dbh.getTimeSpent(issueEntity, status);
        } finally {
            if (dbh != null) {
                dbh.closeConnection();
            }
        }
    }

    public List<IssueEntity> createNewIssue (Context context, IssueEntity issueEntity, ProjectEntity projectEntity) throws DatabaseException {
        IssueDatabaseHelper dbh = null;
        try {
            dbh = new IssueDatabaseHelper(context);
            return dbh.createNewIssue(issueEntity, projectEntity);
        } finally {
            if (dbh != null) {
                dbh.closeConnection();
            }
        }
    }

    public Timestamp getTimeAssigned(Context context, IssueEntity issueEntity, int status) throws DatabaseException {
        TimeLogDatabaseHelper dbh = null;
        try {
            dbh = new TimeLogDatabaseHelper(context);
            return dbh.getTimeAssigned(issueEntity, status);
        } finally {
            if (dbh != null) {
                dbh.closeConnection();
            }
        }
    }
}
