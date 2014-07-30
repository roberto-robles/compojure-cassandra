
$(function(){
    $('#patients').on('change', function(){
        $.ajax({
            url: '/bluebutton/patient',
            data: {
                id: $(this).val()
            },
            type: 'POST',
            dataType: 'json',
            success: function(data){
                result = data[0];
                html = 'Name: ' + result['name'] + '\n';
                html += 'Info: ' + result['info'] + '\n';
                $('#result').val(html);
            }
        });

    });

    $('#sendPatient').on('click', function(){
        $.ajax({
            url: '/bluebutton/patient/add',
            data: {
                id: $('input[name="id"]').val(),
                name: $('input[name="name"]').val(),
                info: $('input[name="info"]').val()
            },
            type: 'POST',
            dataType: 'json',
            success: function(data){
                if (data.success) {
                    alert(data.message);
                }
            }
        });
    });
});
