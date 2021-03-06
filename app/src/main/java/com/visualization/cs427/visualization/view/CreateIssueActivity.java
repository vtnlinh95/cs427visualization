package com.visualization.cs427.visualization.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.visualization.cs427.visualization.Adapter.AssigneeAdapter;
import com.visualization.cs427.visualization.Adapter.EpicAdapter;
import com.visualization.cs427.visualization.DAL.IssueDAL;
import com.visualization.cs427.visualization.Entity.ContributorEntity;
import com.visualization.cs427.visualization.Entity.EpicEntity;
import com.visualization.cs427.visualization.Entity.IssueEntity;
import com.visualization.cs427.visualization.Exception.DatabaseException;
import com.visualization.cs427.visualization.R;
import com.visualization.cs427.visualization.Utils.CurrentProject;
import com.visualization.cs427.visualization.Utils.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateIssueActivity extends AppCompatActivity {
    public void Create(View view)
    {
        IssueEntity entity = getNewIssue();
        try {
            CurrentProject.getInstance().setIssueEntities(IssueDAL.getInstance().createNewIssue(this, entity, CurrentProject.getInstance().getProjectEntity()));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        setResult(SprintDetailActivity.RESULT_OK_NEW_ISSUE);
        finish();

    }
    public void Cancle(View view)
    {
        setResult(SprintDetailActivity.RESULT_CANCEL);
        finish();
    }
    public void AddEpic(View view)
    {
        Intent intent = new Intent(this,AddNewEpic.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_EPIC);
    }

    private Spinner spinnerIssueType,spinnerAssignee,spinnerEpic,spinnerLinkedIssue,spinnerIssue;
    private TextView TextViewAddEpic;
    private Button Create,Cancle;
    private ImageView ImageAddEpic;
    private EditText editTextSummary,editTextStoryPoints,editTextDescription;
    private List<EpicEntity> epicEntities = new ArrayList<>();
    private List<ContributorEntity> contributorEntities = new ArrayList<>();
    public static final int REQUEST_CODE_ADD_EPIC = 100;
    public static final int RESULT_OK_NEW_EPIC = 200;
    public static final int RESULT_CANCEL_NEW_EPIC = 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_issue2);

        /*-------------------------------------findViewById---------------------------------------*/
        spinnerIssueType = (Spinner) findViewById(R.id.spinnerIssueType);
        spinnerAssignee = (Spinner) findViewById(R.id.spinnerAssignee);
        spinnerEpic = (Spinner) findViewById(R.id.spinnerEpic);
        spinnerLinkedIssue = (Spinner) findViewById(R.id.spinnerLinkedIssues);
        spinnerIssue = (Spinner) findViewById(R.id.spinnerIssue);

        ImageAddEpic = (ImageView) findViewById(R.id.ImageAddEpic);
        Create = (Button) findViewById(R.id.Create);
        Cancle = (Button) findViewById(R.id.Cancle);

        TextViewAddEpic = (TextView) findViewById(R.id.TextViewAddEpic);
        editTextSummary = (EditText) findViewById(R.id.editTextSummary);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        editTextStoryPoints = (EditText) findViewById(R.id.editTextStoryPoints);

        String[] items = new String[]{"Story", "Task", "Bug"};
        ArrayAdapter<String> adapterIssiueType = new ArrayAdapter<String>(this, R.layout.simple_list_item_custom, items);
        spinnerIssueType.setAdapter(adapterIssiueType);

        String[] itemsLinedIssues = new String[]{"Blocks", "Is blocked by"};
        ArrayAdapter<String> adapterLinedIssues = new ArrayAdapter<String>(this, R.layout.simple_list_item_custom, itemsLinedIssues);
        spinnerLinkedIssue.setAdapter(adapterLinedIssues);

        setUpEpicSpinner();
        setUpAssigneeSpinner();
        setUpIssueLinkSpinner();
    }

    private IssueEntity getNewIssue (){
        int issueType = spinnerIssueType.getSelectedItemPosition() +1 ;
        String textSummary = editTextSummary.getText().toString();
        int point = Integer.parseInt(editTextStoryPoints.getText().toString());
        ContributorEntity contributorEntity = null;
        if (spinnerAssignee.getSelectedItemPosition() !=0) {
            contributorEntity = contributorEntities.get(spinnerAssignee.getSelectedItemPosition() - 1);
        }
        EpicEntity epicEntity = CurrentProject.getInstance().getEpicEntities().get(spinnerEpic.getSelectedItemPosition());
        List<IssueEntity> list = new ArrayList<>();
        if (spinnerIssue.getSelectedItemPosition() !=0) {
            IssueEntity link = CurrentProject.getInstance().getIssueEntities().get(spinnerIssue.getSelectedItemPosition() - 1);
            list.add(link);
        }
        String description = editTextDescription.getText().toString();
        IssueEntity entity = new IssueEntity(null, textSummary, issueType, point, description, IssueEntity.STATUS_TODO, IssueEntity.LOCATION_BACKLOG, contributorEntity, epicEntity);
        int selectedIssueLink = spinnerIssue.getSelectedItemPosition();
        if (selectedIssueLink == 0){
            return entity;
        }
        if (list.isEmpty()){
            entity.setBlocked(list);
            entity.setBlocker(list);
            return entity;
        }
        if (spinnerLinkedIssue.getSelectedItemPosition() == 0){
            entity.setBlocked(list);
            entity.setBlocker(new ArrayList<IssueEntity>());
        }
        else{
            entity.setBlocked(list);
            entity.setBlocked(new ArrayList<IssueEntity>());
        }
        return entity;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_EPIC){
            if (resultCode == RESULT_OK_NEW_EPIC){
                setUpEpicSpinner();
            }
        }
    }

    private void setUpEpicSpinner (){
        if (!epicEntities.isEmpty()){
            epicEntities.clear();
        }
        epicEntities.addAll(CurrentProject.getInstance().getEpicEntities());
        EpicAdapter adapterEpic = new EpicAdapter(this, epicEntities);
        spinnerEpic.setAdapter(adapterEpic);
    }

    private void setUpAssigneeSpinner(){
        if (!contributorEntities.isEmpty()){
            contributorEntities.clear();
        }
        contributorEntities = DataUtils.getAllContributors(CurrentProject.getInstance().getIssueEntities());
        AssigneeAdapter adapterAssignee = new AssigneeAdapter(this, contributorEntities);
        spinnerAssignee.setAdapter(adapterAssignee);
    }

    private void setUpIssueLinkSpinner(){
        int count = 1;
        List<IssueEntity> issueEntities = CurrentProject.getInstance().getIssueEntities();
        String[] issueNames = new String[issueEntities.size()+1];
        issueNames[0] = "(empty)";
        for (IssueEntity issueEntity : issueEntities){
            issueNames[count] = issueEntity.getName();
            ++count;
        }
        ArrayAdapter<String> adapterIssue = new ArrayAdapter<String>(this, R.layout.simple_list_item_custom, issueNames);
        spinnerIssue.setAdapter(adapterIssue);
    }
}
