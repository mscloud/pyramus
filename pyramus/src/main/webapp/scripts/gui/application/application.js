(function() {

  var applicationSections = $('.form-section');

  $(document).ready(function() {
    
    $.fn.serializeObject = function() {
      var o = {};
      var a = this.serializeArray();
      $.each(a, function() {
        if (o[this.name] !== undefined) {
          if (!o[this.name].push) {
            o[this.name] = [o[this.name]];
          }
          o[this.name].push(this.value || '');
        }
        else {
          o[this.name] = this.value || '';
        }
      });
      return o;
    };    

    // File uploaders
    
    $('.field-attachments-uploader').each(function() {
      var fileInput = $(this).find('input');
      var fileSelector = $(this).find('.field-attachments-selector');
      fileSelector.on('click', function() {
        fileInput.click();
      });
      fileInput.on('change', function() {
        var files = fileInput[0].files;
        for (var i = 0; i < files.length; i++) {
          uploadAttachment(files[i]);
        }
      });
    });
    
    // Dynamic data
    
    $('select[data-source]').each(function() {
      var field = this;
      $.ajax({
        url: $(field).attr('data-source'),
        type: "GET",
        contentType: "application/json; charset=utf-8",
        success: function(result) {
          for (var i = 0; i < result.length; i++) {
            var option = $('<option>').attr('value', result[i].value).text(result[i].text);
            $(field).append(option);
            if ($(field).attr('data-preselect') && result[i].text == $(field).attr('data-preselect')) {
              $(option).prop('selected', true);
            }
          }
        }
      });
    });
    
    // Section checks
    
    $('select[name="field-line"]').on('change', function() {
      var line = $(this).val();
      var isLocalLine = line == 'nettilukio' || line == 'nettipk' || line == 'lahilukio' && line == 'bandilinja';
      $('.section-other-studies').attr('data-skip', !isLocalLine);
      $('.section-attachments').attr('data-skip', !isLocalLine);
    });
    $('input[name="field-birthday"]').on('change', function() {
      var birthday = $(this).val();
      var years = moment().diff(moment(birthday, "D.M.YYYY"), 'years');
      var line = $('select[name="field-line"]').val();
      var isLocalLine = line == 'nettilukio' || line == 'nettipk' || line == 'lahilukio' && line == 'bandilinja';
      $('.section-underage').attr('data-skip', !isLocalLine || years >= 18);
    });
    
    // Custom validators
    
    Parsley.addValidator('birthdayFormat', {
      requirementType: 'string',
      validateString: function(value) {
        return value && moment(value, 'D.M.YYYY').isValid() && value.lastIndexOf('.') == value.length - 5;
      },
      messages: {
        fi: 'Päivämäärän muoto on virheellinen'
      }
    });
    
    Parsley.addValidator('requiredIfShown', {
      requirementType: 'string',
      validateString: function(value, requirement, event) {
        var element = event.element;
        if ($(element).is(':visible')) { 
          if (!value || value.trim().length == 0) {
            return false;
          }
        }
        return true;
      },
      messages: {
        fi: 'Tämä kenttä on pakollinen'
      }
    });
    
    // Dependencies
    
    $('[data-dependencies]').change(function() {
      var name = $(this).attr('name');
      var value = $(this).is(':checkbox') ? $(this).is(':checked') ? $(this).val() : '' : $(this).val();
      $('.field-container[data-dependent-field="' + name + '"]').each(function() {
        var show = false;
        var values = $(this).attr('data-dependent-values').split(',');
        for (var i = 0; i < values.length; i++) {
          show = values[i] == value;
          if (show) {
            break;
          }
        }
        $(this).toggle(show);
      });
    });
    
    // Form navigation

    $('.form-navigation .previous').click(function() {
      var newIndex = curIndex() - 1;
      // TODO better handling for irrelevant sections 
      if ($(applicationSections[newIndex]).attr('data-skip') == 'true') {
        newIndex--;
      }
      navigateTo(newIndex);
    });

    $('.form-navigation .next').click(function() {
      // TODO enable section validation
      //if ($('.application-form').parsley().validate({group: 'block-' + curIndex()})) {
        var newIndex = curIndex() + 1;  
        // TODO better handling for irrelevant sections 
        if ($(applicationSections[newIndex]).attr('data-skip') == 'true') {
          newIndex++;
        }
        navigateTo(newIndex);
      //}
    });

    $('.button-validate').click(function() {
      $('.application-form').parsley().validate({group: 'block-' + curIndex()});
    });

    $('.button-submit').click(function() {
      data = JSON.stringify($('.application-form').serializeObject());
      console.log(data);
    });

    $(applicationSections).each(function(index, section) {
      $(section).find(':input').attr('data-parsley-group', 'block-' + index);
    });
    navigateTo(0);
  });

  function navigateTo(index) {
    // Mark the current section with the class 'current'
    $(applicationSections).removeClass('current').eq(index).addClass('current');
    /*
    $('.form-navigation .previous').toggle(index > 0);
    var atTheEnd = index >= $(applicationSections).length - 1;
    $('.form-navigation .next').toggle(!atTheEnd);
    */
  }

  function curIndex() {
    return $(applicationSections).index($(applicationSections).filter('.current'));
  }
  
  function uploadAttachment(file) {
    var applicationId = $('input[name="application-id"]').val();
    var fileContainer = $('.field-attachments-files'); 
    var formData = new FormData();
    formData.append('file', file);
    formData.append('name', file.name);
    formData.append('applicationId', applicationId);

    var fileElement = $('.application-file.template').clone();
    fileElement.removeClass('template');
    fileContainer.append(fileElement);
    fileElement.show();
    fileElement.find('.applicaton-file-name a').text(file.name);
    fileElement.find('.application-file-size').text((file.size / 1024).toFixed(3) + " KB");
    var fileProgressElement = fileElement.find('.application-file-progress').progressbar({
      value: 0
    });

    $.ajax({
      url: '/1/application/createattachment',
      type: 'POST',
      processData: false,
      contentType: false,
      data: formData,
      xhr: function() {
        myXhr = $.ajaxSettings.xhr();
        if (myXhr.upload) {
          myXhr.upload.addEventListener('progress', function(evt) {
            if (evt.lengthComputable) {
              var percentComplete = (evt.loaded / evt.total) * 100;
              fileProgressElement.progressbar('value', percentComplete);
            }
          }, false);
        }
        return myXhr;
      },
      success: function(data) {
        fileElement.find('.application-file-link').attr('href', '/1/application/getattachment/' + applicationId + '?attachment=' + file.name);
        fileElement.find('.application-file-progress').remove();
        $('.field-attachments-uploader').append($('<input>').attr({type: 'hidden', name: 'attached-file', 'value': file.name}));
      },
      error: function(err) {
        var kerkko = err;
        console.log('pashat');
        fileElement.find('.application-file-progress').remove();
      }
    });
  }
  
}).call(this);