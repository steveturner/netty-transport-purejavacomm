define append_if_no_such_line($file, $line, $refreshonly = 'false') {
   exec { "/bin/echo '$line' >> '$file'":
      unless      => "/bin/grep -Fxqe '$line' '$file'",
      path        => "/bin",
      refreshonly => $refreshonly,
   }
}

class must-have {
  include apt

exec { 'apt-get update':
    command => '/usr/bin/apt-get update',
  }

  package { [
            "bash",
            "wget"
	        ]:
    ensure => present,
    require => Exec["apt-get update"],
  }

  file { "create-local-bin-folder":
    ensure => directory,
    path => "/home/vagrant/bin",
    owner => "vagrant",
    group => "vagrant",
    mode => '755',
    }

  

}

include java7
include must-have

