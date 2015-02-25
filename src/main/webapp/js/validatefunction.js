$(document).ready(function() {
    $('.input-group input[required], .input-group textarea[required], .input-group select[required]').on('keyup change', function() {
		var $form = $(this).closest('form'),
            $group = $(this).closest('.input-group'),
			$addon = $group.find('.input-group-addon'),
			$icon = $addon.find('span'),
			state = false;
            
    	if (!$group.data('validate')) {
			state = $(this).val() ? true : false;
		}else if ($group.data('validate') == "email") {
			state = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/.test($(this).val())
		}else if($group.data('validate') == 'phone') {
			state = /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/.test($(this).val())
		}else if ($group.data('validate') == "length") {
			state = $(this).val().length >= $group.data('length') ? true : false;
		}else if ($group.data('validate') == "number") {
			state = !isNaN(parseFloat($(this).val())) && isFinite($(this).val());
		}

		if (state) {
				$addon.removeClass('danger');
				$addon.addClass('success');
				$icon.attr('class', 'glyphicon glyphicon-ok');
		}else{
				$addon.removeClass('success');
				$addon.addClass('danger');
				$icon.attr('class', 'glyphicon glyphicon-remove');
		}
        
        if ($form.find('.input-group-addon.danger').length == 0) {
            $form.find('[type="submit"]').prop('disabled', false);
        }else{
            $form.find('[type="submit"]').prop('disabled', true);
        }
	});
    
    $('.input-group input[required], .input-group textarea[required], .input-group select[required]').trigger('change');
    
    loadlistAndSelectize($('#DatasetName').get(0),/*the 'select' object*/
     		 'list?type=dataset',/*the url of the server-side script*/
     		 '_alias',/*The name of the field in the returned list*/
     		 'name'
     		 );

    loadlist($('select#DatasetApp').get(0),/*the 'select' object*/
    		 'list?type=folder',/*the url of the server-side script*/
    		 'developerName',/*The name of the field in the returned list*/
    		 'name'
    		 );
        
    $("#uploadForm").on('submit',(function(e) {
    	$("#title2").empty();
    	$("#result").empty();
//    	deleteRow('result');
        e.preventDefault();
        $.ajax({
            url: "upload",
            type: "POST",
            data:  new FormData(this),
            contentType: false,
            cache: false,
            processData:false,
            dataType:  'json', 
            success: function(data){
            	self.location.href = 'logs.html';
            	
//            	$("#title2").append('').html('<h5 style="text-align:center"><i style="color:#0000FF">File is being uploaded check log file below for details</i></h5>');
//            	$("tr:has(td)").remove();
//                $("#result").append(
//                		$('<tr/>')
//                  		.append($('<td/>').text('File'))
//                  		.append($('<td/>').text('File Size'))
//                  		.append($('<td/>').text('File Type'))
//                  		.append($('<td/>').text('File Download Link'))
//                		)//end $("#uploaded-files").append()
//            	for (i = 0; i < data.length; i++) {
//            		var link = "<a href='upload?f="+i+"'>Download</a>";
//            		if(data[i].inputFileType == "Csv")
//            			link = "&nbsp;";
//                    $("#result").append(
//                    		$('<tr/>')
//                      		.append($('<td/>').text(data[i].savedFile))
//                      		.append($('<td/>').text(data[i].inputFileSize))
//                      		.append($('<td/>').text(data[i].inputFileType))
//                      		.append($('<td/>').html(link))
//                    		)//end $("#uploaded-files").append()
//            	}
           },
            error: function (response) {
            	$("#title2").append('').html("<h5 style='text-align:center'><i style='color:#FF0000'>"+response.responseText+"</i></h5>");
          }
       });
    }));
    
});



$(document).on('change', '.btn-file :file', function() {
	  var input = $(this),
	      numFiles = input.get(0).files ? input.get(0).files.length : 1,
	      label = input.val().replace(/\\/g, '/').replace(/.*\//, '');	      
	  input.trigger('fileselect', [numFiles, label]);
	});

	$(document).ready( function() {
	    $('.btn-file :file').on('fileselect', function(event, numFiles, label) {
	        
	        var input = $(this).parents('.input-group').find(':text'),
	            log = numFiles > 1 ? numFiles + ' files selected' : label;
	        
	        if( input.length ) {
	            input.val(log);
	        } else {
	            if( log ) alert(log);
	        }	        
	        input.keyup();
	    });
	});

$(document).ajaxSend(function(event, request, settings) {
		  $('#loading-indicator').show();
		});

$(document).ajaxComplete(function(event, request, settings) {
		  $('#loading-indicator').hide();
});	


function deleteRow(tableID) {
    try {
        var table = document.getElementById(tableID);
        var rowCount = table.rows.length;
        for (var i = 0; i < rowCount; i++) {
            var row = table.rows[i];
             table.deleteRow(i);
             rowCount--;
             i--;
        }
    } catch (e) {
        alert(e);
    }
}

function loadlist(selobj,url,nameattr,displayattr)
{
    $(selobj).empty();
    $.getJSON(url,{},function(data)
    {
        $.each(data, function(i,obj)
        {
            $(selobj).append(
                 $('<option></option>')
                        .val(obj[nameattr])
                        .html(obj[displayattr]));
        });
    });
}

function loadlistAndSelectize(selobj,url,nameattr,displayattr)
{
    $.getJSON(url,{},function(data)
    {
        $(selobj).empty();
        $.each(data, function(i,obj)
        {
            $(selobj).append(
                 $('<option></option>')
                        .val(obj[nameattr])
                        .html(obj[displayattr]));
        });

    	$(selobj).selectize({
    		create: true
    	});
    });
}

function loadDiv(selobj,url,nameattr,displayattr)
{
    $(selobj).empty();
    $.getJSON(url,{},function(data)
    {
        $.each(data, function(i,obj)
        {
            $(selobj).append(
                 $('<div></div>')
                 		.attr('data-value',obj[nameattr])
                 		.attr('data-selectable','')
                 		.attr('class','option')
            			.html(obj[displayattr]));
        });
    });
}