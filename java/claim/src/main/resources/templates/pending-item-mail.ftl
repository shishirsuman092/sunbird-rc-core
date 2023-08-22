<html>
<head>
<style>
    table, th, td {
      border: 1px solid black;
      border-collapse: collapse;
    }

    th, td {
      padding: 10px;
    }
</style>
</head>

<body>
    <p><H3>Hi ${regulatorName}</H3></p>

    <p>Following candidate has applied for registration certificate from your institution as claimed by candidate.</p>

    <p> <div> Candidate list: </div> </p>
    <p>
        <table>
          <tr>
            <th>Name</th>
            <th>Course</th>
            <th>Reference Number</th>
            <th>Registration Number</th>
            <th>Email</th>
          </tr>
          <#list candidates as candidate >
              <tr>
                <td>${candidate.name} </td>
                <td>${candidate.course}</td>
                <td>${candidate.refNo}</td>
                <td>${candidate.registrationNumber}</td>
                <td>${candidate.emailAddress}</td>
              </tr>
          </#list>
        </table>
    </p>

    <div>your response awaited</div>

    <p>Thank you,</p>
    <p>&#60; Registration Credential Issuing Authority &#62;</p>
</body>
</html>
