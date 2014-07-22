package fi.pyramus.rest.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.pyramus.dao.base.TagDAO;
import fi.pyramus.dao.students.StudentGroupDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.domainmodel.users.User;

@Dependent
@Stateless
public class StudentGroupController {
  
  @Inject
  private StudentGroupDAO studentGroupDAO;
  
  @Inject
  private TagDAO tagDAO;
  
  public StudentGroup createStudentGroup(String name, String description, Date beginDate, User user) {
    StudentGroup studentGroup = studentGroupDAO.create(name, description, beginDate, user);
    return studentGroup;
  }
  
  public Tag createStudentGroupTag(StudentGroup studentGroup, String text) {
    Tag tag = tagDAO.findByText(text);
    if (tag == null) {
      tag = tagDAO.create(text);
    }
    studentGroup.addTag(tag);
    return tag;
  }
  
  public List<StudentGroup> listStudentGroups() {
    List<StudentGroup> studentGroups = studentGroupDAO.listAll();
    return studentGroups;
  }
  
  public List<StudentGroup> listUnarchivedStudentGroups() {
    List<StudentGroup> studentGroups = studentGroupDAO.listUnarchived();
    return studentGroups;
  }
  
  public StudentGroup findStudentGroupById(Long id) {
    StudentGroup studentGroup = studentGroupDAO.findById(id);
    return studentGroup;
  }
  
  public Set<Tag> findStudentGroupTags(StudentGroup studentGroup) {
    Set<Tag> tags = studentGroup.getTags();
    return tags;
  }
  
  public StudentGroup updateStudentGroup(StudentGroup studentGroup, String name, String description, Date beginDate, User user) {
    StudentGroup updated = studentGroupDAO.update(studentGroup, name, description, beginDate, user);
    return updated;
  }
  
  public StudentGroup archiveStudentGroup(StudentGroup studentGroup, User user) {
    studentGroupDAO.archive(studentGroup, user);
    return studentGroup;
  }
  
  public StudentGroup unarchiveStudentGroup(StudentGroup studentGroup, User user) {
    studentGroupDAO.unarchive(studentGroup, user);
    return studentGroup;
  }

  public StudentGroup updateStudentGroupTags(StudentGroup studentGroup, List<String> tags) {
    Set<String> newTags = new HashSet<>(tags);
    Set<Tag> studentGroupTags = new HashSet<>(studentGroup.getTags());
    
    for (Tag studentGroupTag : studentGroupTags) {
      if (!newTags.contains(studentGroupTag.getText())) {
        removeStudentGroupTag(studentGroup, studentGroupTag);
      }
        
      newTags.remove(studentGroupTag.getText());
    }
    
    for (String newTag : newTags) {
      createStudentGroupTag(studentGroup, newTag);
    }
    
    return studentGroup;
  }

  public void removeStudentGroupTag(StudentGroup studentGroup, Tag tag) {
    studentGroup.removeTag(tag);
  }

  public void deleteStudentGroup(StudentGroup studentGroup) {
    studentGroupDAO.delete(studentGroup);
  }

}
